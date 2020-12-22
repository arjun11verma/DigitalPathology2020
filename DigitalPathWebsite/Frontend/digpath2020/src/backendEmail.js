const express = require('express'); // HTTP Library for defining responses to calls
const cors = require('cors'); // Library for Cross Origin Resource Sharing 
const bodyParser = require('body-parser'); // Library to parse request bodies
const PORT = 3000;

const gmailAPI = require('nodemailer'); // API for sending emails
const smtpTransport = require('nodemailer-smtp-transport'); // Simple Mail Transfer Protocol Transport

const app = express();
app.use(cors());
app.use(bodyParser.json());

let currentDiagnoses = new Map(); // Map listing all of the current diagnoses being worked on and storing temporary saved data

const emailTransport = gmailAPI.createTransport(smtpTransport({
    service: 'gmail',
    host: 'smtp.gmail.com',
    auth: {
        user: "digitalpathology2020@gmail.com",
        pass: "Timeline123#"
    }
})); // creates gmail API for sending emails

app.post('/api/updateCurrentDiagnosis', (req, res) => {
    var currentUser = false;
    
    if(currentDiagnoses.has(req.body.patientID)) {
        if(req.body.doctorID !== currentDiagnoses.get(req.body.patientID).doctorID) currentUser = true;
        console.log(currentDiagnoses);
    }
    else currentDiagnoses.set(req.body.patientID, {'doctorID': req.body.doctorID, 'currentDiagnosis': "Please input your diagnosis here"});

    res.status(200).send({'using': currentUser, 'currentDiagnosis': currentDiagnoses.get(req.body.patientID).currentDiagnosis});
}); // Defines a POST call for updating the list of current diagnoses

app.post('/api/removeCurrentDiagnosis', (req, res) => {
    currentDiagnoses.delete(req.body.patientID);
    res.status(200).send();
    console.log(currentDiagnoses);
}); // Defines a POST call for removing a diagnosis from the list, signifying its completion 

app.post('/api/saveCurrentDiagnosis', (req, res) => {
    var updatedDiagnosis = currentDiagnoses.get(req.body.patientID);
    updatedDiagnosis.currentDiagnosis = req.body.currentDiagnosis;
    currentDiagnoses.set(req.body.patientID, updatedDiagnosis);

    console.log(currentDiagnoses);
    res.status(200).send();
}); // Defines a POST call for saving a dianogsis before it has been completed

app.post('/api/sendEmail', (req, res) => {
    console.log(req.body);
    emailTransport.sendMail({
        from: "digitalpathology2020@gmail.com",
        to: req.body.address,
        subject: "Cancer diagnosis for " + req.body.name,
        text: "Hello, I hope this message finds you well.\nThis is the cancer diagnosis for " + req.body.name + ". \n" + req.body.message
    }, (error, response) => {
        console.log(error);
        if(error) {
            res.status(500).send(error);
        } else {
            res.status(200).send("Email Sent Successfully!");
        }
    });
}); // Defines a POST call for sending an email to the patient

app.listen(PORT, () => {
    console.log("Listening on port " + PORT);
}); // Runs the server on port 3000