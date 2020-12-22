import React, { Component } from 'react';
import { AppBar, Typography, Grid, Button } from '@material-ui/core';

import { app, check, logoutCurrentUser } from './Database';

class CleanButton extends Component {
    constructor(props) {
        super(props);
        this.state = {

        };
    }

    click = () => {
        if(this.props.logoutTask !== undefined && this.props.logoutTask !== null) this.props.logoutTask();
        if (!this.props.link.includes('//')) document.location.href = this.props.link;
    }

    render() {
        return (
            <div>
                <Button variant="contained" onClick={this.click} style={{ borderRadius: 15, backgroundColor: 'white', fontFamily: "Garamond", margin: "10px" }}>
                    {this.props.text}
                </Button>
            </div>
        );
    }
}

class Home extends Component {
    constructor(props) {
        super(props);
        this.state = {
            email: document.location.href.split('/'),
            length: document.location.href.split('/').length - 1,
            welcomeText: "Welcome to Digital Pathology, a website for expanding global access to pathological diagnosis through modern technology. Please log in."
        };
    }

    componentDidMount = () => {
        check().then((loggedIn) => {
            if(loggedIn && !this.state.email.includes("Homepage")) {
                document.location.href = "./Homepage/" + app.currentUser.profile.email;
            }
        });

        if(this.state.email.includes("Homepage")) {
            this.setState({
                welcomeText: "Welcome user " + this.state.email[this.state.length] + "!"
            })
        }
    }

    render() {
        return (
            <div style={{ height: "100vh" }}>
                <div style={{ backgroundColor: "whitesmoke", height: "100vh" }}>
                    <AppBar style={{ backgroundColor: "lavender", position: "static" }}>
                        <Grid container alignItems="center" direction="row">
                            <Grid item style={{ padding: "10px" }}>
                                <Typography variant="h3" style={{ fontFamily: "Garamond", color: "grey" }}> Digital Pathology </Typography>
                                <Typography variant="h6" style={{ fontFamily: "Garamond", color: "grey" }}> Expanding Oncologic Diagnosis </Typography>
                            </Grid>
                            <CleanButton text="Pathology Portal" link={"./" + this.state.email[this.state.length] + "/PathPortal"}></CleanButton>
                            <CleanButton text="Login" link="./Login"></CleanButton>
                            <CleanButton text="Logout" link = "/" logoutTask = {() => logoutCurrentUser()}></CleanButton>
                        </Grid>
                    </AppBar>

                    <Typography variant = "h5" style = {{fontFamily: "Garamond", margin: 10}}>{this.state.welcomeText}</Typography>
                </div>
            </div>
        );
    }
}



export default Home;