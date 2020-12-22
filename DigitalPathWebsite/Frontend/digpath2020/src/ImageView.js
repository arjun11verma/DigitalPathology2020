import React, { Component } from 'react';
import Zoom from 'react-medium-image-zoom'
import 'react-medium-image-zoom/dist/styles.css'
import { Paper, Grid, Typography, AppBar, TextareaAutosize, Button } from '@material-ui/core';

import { apolloClient } from './ApolloClient';
import gql from 'graphql-tag';
import axios from 'axios';

const APIURL = 'http://localhost:3000';

class ImageViewCard extends Component {
    constructor(props) {
        super(props);
        this.state = {

        };
    }

    render() {
        return (
            <div>
                <Zoom>
                    <img alt="Slide" src={this.props.src} style={{ width: "50vw" }} />
                </Zoom>
            </div>
        );
    }
}

class ImageView extends Component {
    constructor(props) {
        super(props);
        this.state = {
            imageData: null,
            objectId: document.location.href.split('/'),
            emailUser: null,
            nameUser: null,
            determineDiagnosis: <div>
                <Typography variant="body1" style={{ fontFamily: "Garamond", marginLeft: 10 }}>Please input your diagnosis below. You and the patient will both remain anonymous. </Typography>
                <TextareaAutosize
                    variant="outlined"
                    id='diagnosis'
                    style={{ width: 720, height: 300, marginLeft: 10, fontFamily: "Times New Roman" }}
                ></TextareaAutosize>
                <Button onClick={this.sendDiagnosis} style={{ fontFamily: "Garamond", marginLeft: 10 }}>Send Diagnosis!</Button>
                <Button onClick={this.saveData} style={{ fontFamily: "Garamond", marginLeft: 10 }}>Save your diagnosis without sending!</Button>
            </div>,
            diagnosisMessage: "Thank you so much for volunteering to aid the global pathology effort. Please determine a cancer diagnosis from the provided slide image and input the diagnosis below. Once you have successfully completed the diagnosis, please either click Send Diagnosis to send your diagnosis to the patient and make it visible to other doctors."
        };
    }

    queryImage = async () => {
        apolloClient.query({
            query: gql`
            query ImageQuery($_id: ObjectId!) {
                imageSet(query: {_id: $_id}) {
                    image
                    username
                    diagnosis
                    name
                }
            }`,
            variables: { _id: (this.state.objectId[this.state.objectId.length - 1]) }
        }).then((res) => {
            const image = this.processImages(res.data.imageSet.image);
            const diagnosis = res.data.imageSet.diagnosis;
            this.setState({
                imageData: image,
                emailUser: res.data.imageSet.username,
                nameUser: res.data.imageSet.name
            });

            if (diagnosis !== "N") {
                this.setState({
                    determineDiagnosis: <Typography style={{ marginLeft: 10, fontFamily: "Garamond" }}>{("Former Diagnosis: " + diagnosis)}</Typography>,
                    diagnosisMessage: "This patient has already recieved a diagnosis. It can be viewed below."
                });
            } else {
                axios.post(APIURL + '/api/updateCurrentDiagnosis', { 'patientID': this.state.objectId[this.state.objectId.length - 1], 'doctorID': this.state.objectId[this.state.objectId.length - 3] }).then((res) => {
                    console.log(res);
                    if (res.data.using) {
                        this.setState({
                            determineDiagnosis: <Typography style={{ marginLeft: 10, fontFamily: "Garamond" }}>{("Another doctor is currently working on diagnosing this patient.")}</Typography>,
                            diagnosisMessage: "This patient is currently being diagnosed by another pathologist."
                        });
                    } else {
                        console.log(res.data.currentDiagnosis);
                        document.getElementById('diagnosis').value = res.data.currentDiagnosis;
                    }
                });
            }
        }).catch(err => {
            console.log(err);
        });
    }

    processImages = (imgData) => {
        return <ImageViewCard alt="Slide" src={"data:image/jpeg;base64," + imgData} />;
    }

    sendDiagnosis = () => {
        const diagnosis = document.getElementById('diagnosis').value;

        axios.post(APIURL + '/api/sendEmail', { 'address': "arjunverma1com@gmail.com", 'name': this.state.nameUser, 'message': diagnosis }).then((res) => {
            if (res.status === 200) {
                apolloClient.mutate({
                    mutation: gql`
                                mutation updateImageSet($_id: ObjectId!, $ImageSetUpdateInput: ImageSetUpdateInput!) {
                                    updateOneImageSet(query: {_id: $_id}, set: $ImageSetUpdateInput) {
                                        _id
                                    }
                                }`,
                    variables: {
                        _id: (this.state.objectId[this.state.objectId.length - 1]),
                        ImageSetUpdateInput: {
                            "diagnosis": diagnosis
                        }
                    }
                }).then((response) => {
                    axios.post(APIURL + '/api/removeCurrentDiagnosis', { 'patientID': this.state.objectId[this.state.objectId.length - 1] }).then((r) => {
                        document.location.href = "/";
                    });
                }).catch((err) => {
                    console.log(err);
                });
            } else {
                console.log("There was an error with the server");
                console.log(res);
            }
        });
    }

    saveData = async() => {
        axios.post(APIURL + '/api/saveCurrentDiagnosis', { 'patientID': this.state.objectId[this.state.objectId.length - 1], 'currentDiagnosis': document.getElementById('diagnosis').value }).then((res) => {
            window.location.href = '/';
        });
    }

    componentDidMount = () => {
        this.queryImage();
    }

    render() {
        return (
            <div>
                <AppBar style={{ backgroundColor: "lavender", position: "static" }}>
                    <Grid container alignItems="center" direction="row">
                        <Grid item style={{ padding: "10px" }}>
                            <Typography variant="h3" style={{ fontFamily: "Garamond", color: "grey" }}> Digital Pathology </Typography>
                            <Typography variant="h6" style={{ fontFamily: "Garamond", color: "grey" }}> Expanding Oncologic Diagnosis </Typography>
                        </Grid>
                        <Grid item alignItems="center" style={{ marginLeft: "auto" }}>
                            <Typography variant="h3" style={{ fontFamily: "Garamond", color: "grey", margin: 10 }}>Slide Image Diagnosis</Typography>
                        </Grid>
                    </Grid>
                </AppBar>

                <Grid container direction="row">
                    <Grid xs={6}>
                        <Paper style={{ height: "640px", position: "static" }}>
                            <Typography variant="body1" style={{ fontFamily: "Garamond", marginLeft: 10 }}>{this.state.diagnosisMessage} </Typography>
                            <br />
                            {this.state.determineDiagnosis}
                        </Paper>
                    </Grid>

                    <Grid xs={6}>
                        <Paper style={{ height: "640px", position: "static" }}>
                            {this.state.imageData}

                            <Typography variant="h6" style={{ fontFamily: "Garamond", textAlign: "center" }}>
                                Click on the image above to pan and zoom full screen.
                            </Typography>
                        </Paper>
                    </Grid>
                </Grid>
            </div>
        );
    }
}

export default ImageView;