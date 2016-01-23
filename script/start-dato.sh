#!/bin/bash
export DATOMIC_URI="datomic:free://localhost:4334/pc2" # This will depend on your datomic setup
export NREPL_PORT=6005 # starts embedded nrepl server
export DATO_PORT=8081 # 8080 is the default value, change it if there is a port conflict
export PORT=10556 # 10555 is the default
lein run
