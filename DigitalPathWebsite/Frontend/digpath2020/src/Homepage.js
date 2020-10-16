import React, { Component } from 'react'
import axios from 'axios'

class Homepage extends Component {
    constructor(props) {
        super(props);
        this.state = {
            imgList: []
        }
    }

    componentDidMount = () => {
        this.processImages();
    }

    printImages = () => {
        for (var i = 0; i < 20; i++) {
            axios.post('http://127.0.0.1:5000/returnImages', { 'index': i }).then(res => {
                var imgData = "data:image/jpeg;base64," + (res.data);
                var tempList = this.state.imgList;

                var img = React.createElement("img", {key: "image", src: imgData, width: 400, height: 400}, null);

                tempList.push(img);

                this.setState({
                    imgList: tempList
                });

                console.log(this.state.imgList);
            });
        }
    }

    processImages = () => {
        axios.post('http://127.0.0.1:5000/processImages', {'name': 'testuserone'}).then(res =>{
            console.log(res.data);
        });
    }

    render() {
        return (
            <div>
                <div>{this.state.imgList}</div>
            </div>
        )
    }
}

export default Homepage;