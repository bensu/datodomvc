(ns ^:figwheel-always datodomvc.client.core
    (:require [cljs.core.async :as async :refer [put! chan <!]]
              [datascript :as d]
              [dato.lib.core :as dato]
              [dato.lib.controller :as con]
              [dato.db.utils :as dsu]
              [dato.lib.db :as db]
              [datodomvc.client.components.root :as com-root]
              [datodomvc.client.routes :as routes]
              [datodomvc.client.utils :as utils]
              [datodomvc.client.config :as config]
              [om.core :as om :include-macros true])
    (:require-macros [cljs.core.async.macros :as async :refer [alt! go go-loop]]))

;; Put this here so it's easy to query in the repl
(defonce conn
  (d/create-conn))

(enable-console-print!)

(defn handle-mouse-move [cast! event]
  (cast! {:event :ui/mouse-moved
          :data [(.. event -pageX)
                 (.. event -pageY)]}))

(defn handle-mouse-down [cast! event]
  (cast! {:event :ui/mouse-down
          :data  [(.. event -pageX)
                  (.. event -pageY)]}))

(defn handle-mouse-up [cast! event]
  (cast! {:event :ui/mouse-up
          :data  [(.. event -pageX)
                  (.. event -pageY)]}))

(defonce ^:export app-state
  ;; TODO: Ugh, why is this like this?
  (let [ws-host  (str js/window.location.hostname)
        ws-port  (config/dato-port)
        ws-path  "/ws/index.html"
        app-dato (dato/new-dato ws-host ws-port ws-path conn {})]
    (atom {:dato app-dato})))

(defn ^:export -main
  ([root-node state]
   (-main root-node state {}))
  ([root-node state opts]
   (js/console.log "Installing in " root-node)
   (let [container (utils/sel1 root-node "div.app-instance")
         dato      (:dato @state)
         dato-take (get-in dato [:comms :dato-take])
         conn      (get-in dato [:conn])
         cast!     (get-in dato [:api :cast!])]
     (let [app-root (om/root com-root/root-com state
                             {:target     container
                              :shared     {:dato dato}
                              :instrument (:om-instrument opts)})]
       (when-not (dato/bootstrapped? @conn)
         #_(js/window.addEventListener "mousemove" (partial handle-mouse-move cast!) false)
         (js/window.addEventListener "dragover" #(.preventDefault (or % js/event)) false)
         (js/window.addEventListener "drop" #(.preventDefault (or % js/event)) false)

         ;; Grab the DataScript Schema (and enums), and the session ID to
         ;; start.
         (dato/bootstrap! dato)
         (go
           ;; Wait until we have the schema and session-id
           (let [[bootstrap-success? session-id] (<! dato-take)
                 {:keys [r-qes-by]}              @(:ss dato)]
             (let [router (routes/make-router dato)]
               (r-qes-by {:name :find-tasks
                          :a    :dato/type
                          :v    :datodomvc.types/task})
               (dato/start-loop! dato {:root container
                                       :router router}))))
         ;; Listen to all transactions
         (let [dato-take (async/chan)
               f (fn [db]
                   (let [tasks (dsu/qes-by db :task/title)]
                     (println "tasks:" (map #(into {:db/id (:db/id %)} %) tasks))
                     (count tasks)))]
           (async/tap (get-in dato [:comms :dato-mult]) dato-take)
           (go-loop [db @(:conn dato)]
             (let [payload (<! dato-take)]
               (if (vector? payload)
                 (recur db)
                 (recur (if-let [tx-data (get-in payload [:data :tx])]
                          (let [db' (d/db-with db tx-data)]
                            (println tx-data)
                            (println "f" (f db'))
                            db')
                          db)))))))
       app-root))))
