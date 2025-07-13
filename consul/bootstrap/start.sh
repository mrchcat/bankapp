#!/bin/sh

/tmp/bootstrap/init.sh &

consul agent -dev -node=myNode -client=0.0.0.0 -log-level=INFO