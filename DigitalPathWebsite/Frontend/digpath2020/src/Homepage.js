import React, { Component } from 'react'
import * as Realm from "realm-web";
import axios from 'axios'

import {app} from './Database';

const server_url = 'http://127.0.0.1:5000';

class Homepage extends Component {
    constructor(props) {
        super(props);
        this.state = {
            imgList: []
        }
    }

    componentDidMount = () => {
        if(app.currentUser.isLoggedIn) {
            this.printImages();
        }
    }

    printImages = async() => {
        console.log(app);

        const mongo_db = app.services.mongodb("DigPath2020");
        
        const mongo_collection = mongo_db.db("digitalpath2020").collection("ImageSet");

        var imgData = await mongo_collection.find({username: "arjun@gmail.com"});
        console.log(imgData);

        var base64_data = "";
        imgData.forEach(json_data => {
            base64_data = json_data['image'];
            imgData.push(React.createElement("img", { key: "image", src: ("data:image/jpeg;base64," + base64_data)}, null));
        });

        this.setState({
            imgList: imgData
        });
    }

    render() {
        return (
            <div>
                <div>{this.state.imgList}</div>
            </div>
        );
    }
}

export default Homepage;