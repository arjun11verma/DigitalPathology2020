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
        this.printImages();
    }

    printImages = () => {
        axios.post('http://127.0.0.1:5000/displayImages', { 'username': 'arjun@gmail.com' }).then(res => {
            var imgData = "data:image/jpeg;base64," + (res.data);
            var tempList = this.state.imgList;

            var img = React.createElement("img", { key: "image", src: imgData, width: 400, height: 400 }, null);

            tempList.push(img);

            this.setState({
                imgList: tempList
            });
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