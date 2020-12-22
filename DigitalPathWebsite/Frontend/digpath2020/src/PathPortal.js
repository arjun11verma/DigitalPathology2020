import React, { Component } from 'react';
import { Paper, Typography, Grid, AppBar, TextField, CardActionArea } from '@material-ui/core';

import { apolloClient } from './ApolloClient'
import { check } from './Database';
import gql from "graphql-tag";

class SlideView extends Component {
    constructor(props) {
        super(props);
        this.state = {

        }
    }

    showImage = () => {
        document.location.href = ('./ImageView/' + this.props.id);
    }

    render() {
        return (
            <div>
                <Paper style={{ backgroundColor: "lavender", borderRadius: 5, margin: 10 }}>
                    <CardActionArea onClick = {this.showImage}>
                        <Grid container direction="row">
                            <Typography variant="body1" style={{ fontFamily: "Garamond", margin: 5 }}>Slide Type: {this.props.slide} </Typography>
                            <Typography variant="body1" style={{ fontFamily: "Garamond", margin: 5 }}>Cancer Type: {this.props.cancer} </Typography>
                            <Typography variant="body1" style={{ fontFamily: "Garamond", margin: 5 }}>Date Recorded: {this.props.date} </Typography>
                            <Typography variant="body1" style={{ fontFamily: "Garamond", margin: 5 }}>Diagnosis recieved: {this.props.diagnosis} </Typography>
                        </Grid>
                    </CardActionArea>
                </Paper>
            </div>
        )
    }
}

class PathPortal extends Component {
    constructor(props) {
        super(props);
        this.state = {
            username: document.location.href.split('/')[4],
            failedCancer: false,
            failedSlide: false,
            activeSlides: "Nothing Selected."
        }
    }

    makeSlideBox = (slide, cancer, date, id, diagnosis) => {
        return (
            <SlideView slide={slide} cancer={cancer} date={date} id={id} diagnosis = {diagnosis} />
        )
    }

    queryName = async (input) => {
        apolloClient.query({
            query: gql`
            query ImageQuery($name: String!) {
                imageSets(query: {name: $name}) {
                    cancer
                    slide
                    timestamp
                    _id
                }
            }`,
            variables: { name: input }
        }).then((res) => {
            return res.data.ImageSet.objectId;
        }).catch(err => {
            console.log(err);
            return "";
        });
    }

    queryCancer = async (input) => {
        apolloClient.query({
            query: gql`
            query ImageQuery($cancer: String!) {
                imageSets(query: {cancer: $cancer}) {
                    cancer
                    slide
                    timestamp
                    _id
                }
            }`,
            variables: { cancer: input }
        }).then((res) => {
            return res.data.ImageSet.objectId;
        }).catch(err => {
            console.log(err);
            return "";
        });
    }

    querySlide = async (input) => {
        apolloClient.query({
            query: gql`
            query ImageQuery($slide: String!) {
                imageSets(query: {slide: $slide}) {
                    cancer
                    slide
                    timestamp
                    _id
                }
            }`,
            variables: { slide: input }
        }).then((res) => {
            return res.data.ImageSet.objectId;
        }).catch(err => {
            console.log(err);
            return "";
        });
    }

    componentDidMount = () => {
        check().then((loggedIn) =>{
            if(!loggedIn) document.location.href = "/";
        })

        apolloClient.query({
            query: gql`
            query ImageQuery {
                imageSets {
                    cancer
                    slide
                    timestamp
                    _id
                    diagnosis
                }
            }`
        }).then((res) => {
            const response = res.data.imageSets;
            console.log(response);
            var activeImages = [];
            response.forEach(data => {
                activeImages.push(this.makeSlideBox(data.slide, data.cancer, data.timestamp, data._id, data.diagnosis === "N" ? "No" : "Yes"));
            });
            this.setState({
                activeSlides: activeImages
            })
        }).catch(err => {
            console.log(err);
            return "";
        });
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
                        <Grid item alignItems = "center" style = {{marginLeft: "auto"}}>
                            <Typography variant="h3" style={{ fontFamily: "Garamond", color: "grey", margin: 10}}>Pathology Portal</Typography>
                        </Grid>
                    </Grid>
                </AppBar>

                <Grid container direction="row" style={{ }}>
                    <Grid xs={6}>
                        <Paper style={{ height: "630px", overflowY: "hidden" }}>
                            <Typography variant="h5" style={{ fontFamily: "Garamond", margin: 10 }}>
                                Welcome to the Pathology Portal {this.state.username.split('@')[0]}!
                            </Typography>

                            <Typography variant="h6" style={{ fontFamily: "Garamond", margin: 10 }}>
                                Here, you can view the Whole Slide Images of several patients who have not yet recieved diagnoses. The whole slide images are listed to the right, and you can search for specific types of slides or cancers below. Thank you for contributing to the Global Pathology effort!
                            </Typography>

                            <TextField
                                variant="outlined"
                                margin="normal"
                                label="Enter Cancer Type"
                                id="cancer"
                                error={this.state.failedCancer}
                                style={{ width: "60%", margin: 10 }}
                            />

                            <TextField
                                variant="outlined"
                                margin="normal"
                                label="Enter Slide Type"
                                id="slide"
                                error={this.state.failedSlide}
                                style={{ width: "60%", margin: 10 }}
                            />
                        </Paper>
                    </Grid>

                    <Grid xs={6}>
                        <Paper style={{ height: "630px", overflowY: "scroll" }}>
                            {this.state.activeSlides}
                        </Paper>
                    </Grid>
                </Grid>
            </div>
        );
    }
}

export default PathPortal;