import { S3Client } from "@aws-sdk/client-s3";
import { GetObjectCommand } from "@aws-sdk/client-s3";

const region = "us-east-1";

const client = new S3Client({region: region});


// https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-s3/classes/getobjectcommand.html
// How to get data from an S3 bucket
// Probably make singelton instance of someting like dis in the backend
// client.send(new GetObjectCommand(new GetObjectCommand))

