# Image Recognition

## Features

In order to upload the picture first send POST request with `content_type` and optionally provided `callback_url` in
request body to `/blobs`. The response with contain a unique `upload_url`, along with the `blob_id`.

Using this url send a PUT request containing the image. Once the image is uploaded the image recognition starts. Only
the results with confidence 80.0 and more will be saved. Once it is finishes it will POST request to
the `callback_url` (if specified).

Additionally, the user may send a GET request to `/blobs/{blob_id}` in order to get the recognition results.

You may find the API definition [here](TODO).

## Implementation

![Solution Diagram](./diagram.png)

## Deployment

The Serverless framework is used for describing infrastructure. The user is able to deploy the service to any account
with sls deploy.

## Development

Just build the project in order for the pre-compile weaving of dagger to take place.