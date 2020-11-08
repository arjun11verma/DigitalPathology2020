var MongoClient = require('mongodb').MongoClient;
var ObjectID = require('mongodb').ObjectID;

var collectionName = "ImageSet";

var uriString = "mongodb+srv://averma332:Timeline123#@digpath2020.mujb1.mongodb.net/digitalpath2020?retryWrites=true&w=majority";

var c;
var db;
MongoClient.connect(uriString, function (err, database) {
    if (err) {
        console.log(err);
    }
    console.log("Connected correctly to mongo");
    db = database;
    c = db.collection(collectionName);
});

var handlers = {
    readOne: function(req, res) {
        var reqUsername = req.username;
        c.find({ username: reqUsername}, function(err, doc) {
            if(err) {
                console.log(err);
                return null;
            } else {
                return doc;
            }
        }); 
    }
}

module.exports = handlers;
