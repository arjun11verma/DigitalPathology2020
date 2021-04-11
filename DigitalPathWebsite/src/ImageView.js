/**
 * Class for the Slide Image Viewing and Diagnosis Page
 * @version 1.0
 * @author Arjun Verma
 */

import React, { Component } from 'react';
import Zoom from 'react-medium-image-zoom';
import 'react-medium-image-zoom/dist/styles.css';
import { Paper, Grid, Typography, AppBar, TextareaAutosize, Button } from '@material-ui/core';

import { apolloClient } from './Backend/ApolloClient';
import gql from 'graphql-tag';
import axios from 'axios';
import {server_url} from 'axios';
import { check } from './Backend/Database';
import ImageMapper from 'react-image-mapper';
import './tooltip.css'

const imgProcServerURL = 'http://127.0.0.1:5000';

/**
 * Inner class for displaying the image
 */
class ImageViewCard extends Component {
    /**
     * Constructor for the class
     * @param {*} props 
     */
    constructor(props) {
        super(props);
        this.state = {
            mitosisData: props.mitosisData,
            hoveredArea: null,
            displayMitosis: false,
            imgHeight: 600
        };
    }



    /**
     * Renders the Image UI Component
     */
    render() {
        let image = null;
        if (this.state.displayMitosis) {
            image = this.renderMitosisMap();
        } else {
            image = this.renderZoomImage();
        }
        return (
            <div classname = "container">
                {image}
                <Button onClick={this.toggleMitosis} style={{ fontFamily: "Garamond", marginLeft: 10 }}>
                    {this.state.displayMitosis ? 'Disable Mitosis Overlay': 'Enable Mitosis Overlay'}
                </Button>

            </div>
        )
    }

    renderZoomImage() {
        return (
            <Zoom style = {{height: this.state.imgHeight}}>
                <img alt="Slide" src={this.props.src} style={{ width: "50vw", height: 600}} />
            </Zoom>
        );
    }

    renderMitosisMap() {
        return (
                <div style={{ position: "relative" }}>
                    <ImageMapper
                        src={this.props.src}
                        map={this.makeInteractiveMap(this.state.mitosisData)}
                        height = {600}
                        onMouseEnter={area => this.enterArea(area)}
                        onMouseLeave={area => this.leaveArea(area)}
                        lineWidth={1}
                        strokeColor={"red"}
                    />
                    {this.state.hoveredArea && (
                        <span
                            className="tooltip"
                            style={{ ...this.getTipPosition(this.state.hoveredArea) }}
                        >
                            {this.state.hoveredArea && this.state.hoveredArea.name}
                        </span>
                    )}
                </div>   
        );
    }

    toggleMitosis = () => {
        this.setState({
            displayMitosis: !this.state.displayMitosis
        });
    }

    makeInteractiveMap(mitosisData) {
        let areas = []
        let width = mitosisData.regionWidth;
        let height = mitosisData.regionHeight;
        let probs = mitosisData.probs;
        for (let row = 0; row < probs.length - 1; row++) {
            for (let col = 0; col < probs[row].length - 1; col++) {
                let coords = [width * col, height * row, width * (col + 1), height * (row + 1)]
                let shape = "rect"
                let name = "Mitosis Prob: " + probs[row][col];
                areas.push({name: name, coords: coords, shape: shape})
            }
        }
        
        let map = {name: 'mitosisProbs', areas: areas};
        return map;
    }

    getInitialState() {
		return { hoveredArea: null, msg: null, moveMsg: null };
	}

	
	enterArea(area) {
		this.setState({
			hoveredArea: area
		});
	}

	leaveArea(area) {
		this.setState({
			hoveredArea: null
		});
	}

	getTipPosition(area) {
        let yOffset = 50;
        if (area.center[1] < this.state.imgHeight - yOffset) {
            return { top: `${area.center[1] + yOffset}px`, left: `${area.center[0]}px` };
        } else {
            return { top: `${area.center[1] - yOffset}px`, left: `${area.center[0]}px` };
        }
	}
    
}

const url_list = document.location.href.split('/');

class ImageView extends Component {
    /**
     * Constructor for the class
     * Sets the state to contain a String of image data, a String list representing the URL (which contains the objectID of the image data),
     * a String for the patient's email, a String for the patient's name, a React Component for the diagnosis, and a String for a message above the diagnosis
     * @param {*} props 
     */
    constructor(props) {
        super(props);
        this.state = {
            imageData: null,
            imageName: null,
            objectId: url_list[url_list.length - 1],
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

    /**
     * Queries the database for the image and user data from the state objectId
     */
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
            variables: { _id: this.state.objectId }
        }).then(async (res) => {
            const diagnosis = res.data.imageSet.diagnosis;
            this.setState({
                emailUser: res.data.imageSet.username,
                nameUser: res.data.imageSet.name,
                imageName: res.data.imageSet.name
            });
            const image = await this.processImages(res.data.imageSet.image);
            this.setState({
                imageData: image
            })
            if (diagnosis !== "N") {
                this.setState({
                    determineDiagnosis: <Typography style={{ marginLeft: 10, fontFamily: "Garamond" }}>{("Former Diagnosis: " + diagnosis)}</Typography>,
                    diagnosisMessage: "This patient has already recieved a diagnosis. It can be viewed below."
                });
            } else {
                axios.post(server_url + 'updateCurrentDiagnosis', { 'patientID': this.state.objectId[this.state.objectId.length - 1], 'doctorID': this.state.objectId[this.state.objectId.length - 3] }).then((res) => {
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

    /**
     * Creates an ImageView Component from the given image data
     * @param {String} imgData 
     */
    processImages = async (imgData) => {
        let mitosisResp = await this.getMitosisProb(this.state.emailUser, this.state.imageName);
        let mitosisData = mitosisResp.data;
        return <ImageViewCard alt="Slide" src={"data:image/jpeg;base64," + imgData } mitosisData={mitosisData} />;
    }

    /**
     * 
     * @param {String} username the username which may have access to the given image
     * @param {String} imgName 
     */
    getMitosisProb = async (username, imgName) => {
        return axios.get(imgProcServerURL + '/getMitosisProb', {
            params: {
                username: username,
                name: imgName
            }
        });
    }

    /**
     * Sends the diagnosis typed in by the doctor to the patient and saves it to the database
     */
    sendDiagnosis = () => {
        const diagnosis = document.getElementById('diagnosis').value;

        axios.post(server_url + 'sendEmail', { 'address': "arjunverma1com@gmail.com", 'name': this.state.nameUser, 'message': diagnosis }).then((res) => {
            if (res.status === 200) {
                apolloClient.mutate({
                    mutation: gql`
                                mutation updateImageSet($_id: ObjectId!, $ImageSetUpdateInput: ImageSetUpdateInput!) {
                                    updateOneImageSet(query: {_id: $_id}, set: $ImageSetUpdateInput) {
                                        _id
                                    }
                                }`,
                    variables: {
                        _id: (this.state.objectId),
                        ImageSetUpdateInput: {
                            "diagnosis": diagnosis
                        }
                    }
                }).then((response) => {
                    axios.post(server_url + 'removeCurrentDiagnosis', { 'patientID': this.state.objectId }).then((r) => {
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

    /**
     * Saves the temporary diagnosis of a doctor
     */
    saveData = async() => {
        axios.post(server_url + 'saveCurrentDiagnosis', { 'patientID': this.state.objectId, 'currentDiagnosis': document.getElementById('diagnosis').value }).then((res) => {
            window.location.href = '/';
        });
    }

    /**
     * Calls the query image method when the page is opened
     */
    componentDidMount = () => {
        check().then((loggedIn) => {
            if(!loggedIn) window.location.href = "/";
        });

        this.queryImage();
    }

    /**
     * Renders the UI Components of the page
     */
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