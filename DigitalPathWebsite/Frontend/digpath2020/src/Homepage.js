import {apolloClient} from './ApolloClient'
import React, { Component } from 'react';
import {check} from './Database';
import gql from "graphql-tag";

class Homepage extends Component {
    constructor(props) {
        super(props);
        this.state = {
            username: "arjun@gmail.com",
            imgList: []
        }
    }

    componentDidMount = () => {
        if(!check()) document.location.href = ('/');
        apolloClient.query({
            query: gql`
            query {
                imageSet(query: {name: "George"}) {
                    image
                }
            }`
        }).then((res) => {
            console.log(res);
        }).catch(err => {
            console.log(err);
        });
    }

    processImages = (imgData) => {
        imgData.forEach(json_data => {
            imgData.push(React.createElement("img", { key: "image", src: ("data:image/jpeg;base64," + json_data)}, null));
        });
        
        return imgData;
    }

    render() {
        return (
            <div>
                
            </div>
        );
    }
}

export default Homepage;