#!/bin/bash
set -e

DATE=$(date +%Y%m%d)
COMMIT=$(git rev-parse --short HEAD)
IMAGE_TAG="${DATE}-${COMMIT}"

echo "IMAGE_TAG=$IMAGE_TAG"
echo "$IMAGE_TAG" > image_tag.txt
