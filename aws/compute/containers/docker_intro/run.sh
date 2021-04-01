#!/usr/bin/env bash

# Run docker images to verify that the image was created correctly.
docker images --filter reference=hello-world

# Run the image
docker run -t -i -p 80:80 hello-world