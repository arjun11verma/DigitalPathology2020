import React, { Component } from 'react';
import {Grid, Typography, Container, Paper, TextField, Button, Link} from '@material-ui/core';

import {logIn} from './Database';

class LoginPage extends Component {
    constructor(props) {
        super(props);
        this.state = {
            usernameFailed: false,
            passwordFailed: false
        }
    }

    login = async() => {
        const email = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        try {
            await logIn(email, password); 
            document.location.href = ('/Homepage/' + email);
        } catch (error) {
            this.setState({
                passwordFailed: true,
                usernameFailed: true
            });
        }
    }

    render() {
        return (
            <div style={{height: "100vh"}}>
                <div style = {{backgroundColor: "whitesmoke", height: "100vh"}}>
                    <Container>
                        <Typography style={{paddingTop: "10%", marginBottom: "-5%", textAlign: "center"}} component="h1" variant="h3">
                            Digital Pathology Portal
                        </Typography>
                    </Container>
                    <Container component = "main" maxWidth = "sm" style={{paddingTop: "5%"}}>
                        <Paper style={{backgroundColor: "white", display: "flex", flexDirection: "column", alignItems: "center"}} elevation={24}>
                            <form>
                                <TextField
                                    variant="outlined"
                                    margin="normal"
                                    required
                                    label="Username"
                                    id="username"
                                    error = {this.state.usernameFailed}
                                    autoFocus
                                    style={{width: "80%", marginLeft: "10%"}}
                                />
                                <TextField
                                    variant="outlined"
                                    margin="normal"
                                    required
                                    fullWidth
                                    label="Password"
                                    type="password"
                                    id="password"
                                    error = {this.state.passwordFailed}
                                    style={{width: "80%", marginLeft: "10%"}}
                                />
                                <Button
                                    onClick = {this.login}
                                    fullWidth
                                    variant="contained"
                                    style={{width: "80%", marginLeft: "10%", marginTop: "3%", marginBottom: "5%"}}
                                >
                                    Sign In
                                </Button>
                                <Grid container style={{marginBottom: "5%"}}>
                                    <Grid item xs>
                                        <Link style={{width: "80%", marginLeft: "10%", marginTop: "5%", marginBottom: "5%"}} href="/CreateAccount">
                                            {"Don't have an account? Sign up!"}
                                        </Link>
                                    </Grid>
                                </Grid>
                            </form>
                        </Paper>
                    </Container>
                </div>
            </div>
        );
    }
}

export default LoginPage;