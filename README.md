# DatodoMVC

A [TodoMVC](http://todomvc.com/) implementation using [Dato](https://github.com/sgrove/dato), the Clojure/Script Datomic/DataScipt app development framework.

This is an implementation of TodoMVC using Datomic on the backend to persist data that's then fed to DataScript in the frontend. The UI is driven entirely from the DataScript DB (even component local state). See the [Dato Rationale](https://github.com/sgrove/dato#rationale) for more. [Here's a video](https://www.youtube.com/watch?v=7bAdBXfZtZU) of what it looks like once it's running

## Running this demo

1. Install and start Datomic.

   Datomic is not open source and it's
   artifacts are not hosted in Clojars. When developing, there are two
   parts to Datomic, the Transactor and the Peer API library. The
   Transactor is a process that you need to run alongside Dato's
   server, just like any other database. The Peer API library is a jar
   that gets included in Dato's project. To get both things, make
   an account [here](http://www.datomic.com/get-datomic.html). You
   will get credentials, a username and a password. Set up your
   Datomic credentials under `~/.lein/credentials.clj.pgp` as
   described
   [here](http://docs.datomic.com/getting-started.html#lein-setup), so
   that Leiningen can fetch the Peer API library when starting the server.
   If you run into trouble with Leiningen + GPG, read
   [this](https://github.com/technomancy/leiningen/blob/master/doc/GPG.md).
   The Transactor is an executable that you can [download](https://my.datomic.com/downloads/free) to your
   machine and start from the command line. Unzip, it and run it:

```sh
unzip /your/download/directory/datomic-free-0.9.5206.zip -d
/your/chosen/directory/Datomic/
cd /your/chosen/directory/Datomic/datomic-free-0.9.5206/ 
./bin/transactor config/samples/free-transactor-template.properties
```

You should see the following output:

```
Launching with Java options -server -Xms1g -Xmx1g -XX:+UseG1GC -XX:MaxGCPauseMillis=50
Starting datomic:free://localhost:4334/<DB-NAME>, storing data in: data ...
System started datomic:free://localhost:4334/<DB-NAME>, storing data in: data
```

Since the command is long and hard to get right, you might want to
write a small `script/datomic`, so that you can
then call `./script/datomic` from Dato's top level

```
mkdir script
touch script/datomic
# copy the command into script/datomic 
chmod +x script/datomic # make it executable
```

2. Clone this repo

    ```
    git clone git@github.com:sgrove/datodomvc.git
    cd datodomvc
    git submodule update --init
    ```

3. Start the figwheel server. Figwheel compiles the frontend assets, recompiles them when the files change, and updates the browser with the changed code.

   `lein figwheel` or `rlwrap lein figwheel`

If you've failed to set up Datomic's credentials, any `lein` command
will fail.

4. Start the web server. Export a few environment variables to get things working on your system. The DatodoMVC example uses environ, so you can also define env variables in a local profiles.clj.

    ```
    export DATOMIC_URI="datomic:free://localhost:4334/pc2" # This will depend on your datomic setup
    export NREPL_PORT=6005 # starts embedded nrepl server
    export DATO_PORT=8081 # 8080 is the default value, change it if there is a port conflict
    export PORT=10556 # 10555 is the default
    lein run
    ```

## Support

 * For questions on getting this demo running or modifying it, feel free to open issues on this repo.
 * For overall Dato design issues, please use the [Dato Repo's issue tracker](https://github.com/sgrove/dato/issues)
 * General questions, I'm on twitter at [@sgrove](https://twitter.com/sgrove)

## License

Copyright © 2015 Sean Grove

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
