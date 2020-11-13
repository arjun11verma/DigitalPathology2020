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
            var response_data = res.data;
            response_data = response_data['image_list'];

            var imgData = "data:image/jpeg;base64," + (response_data[0]);
            var tempList = this.state.imgList;

            var img = React.createElement("img", { key: "image", src: imgData}, null);

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