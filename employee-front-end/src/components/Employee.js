import React, { Component } from "react";
import UploadService from "../services/upload-files.service";
import 'bootstrap/dist/css/bootstrap.css';
import Modal from 'react-bootstrap/Modal';
import Button from 'react-bootstrap/Button';
import { Container } from "react-bootstrap";

import AuthService from "../services/auth.service";
import { Helmet } from "react-helmet";
import Navigation from "./Navigation";

export default class UploadFiles extends Component {
  constructor(props) {
    super(props);

    this.selectFile = this.selectFile.bind(this);
    this.upload = this.upload.bind(this);

    this.state = {
      selectedFiles: undefined,
      currentFile: undefined,
      fileInfos: [],
      showModal: false,
      show: false,
      errorMessage: [],
      currentUser: undefined,
      erM: Object,

    };
  }
 Profile = () => {
     
     this.setState({
      currentUser: AuthService.getCurrentUser(),
    });
  }
// show <Modal>
  showError = () => {
    this.setState({
      showModal: true,
    });
  }
// close <Modal>
  closeError = () => {
    this.setState({
      showModal: false,
    });

  }
// set target file
  selectFile(event) {
    this.setState({
      selectedFiles: event.target.files,
      fileInfos: [],
    });
  }
// send file to backEnd API and return response from API / catch error
  upload() {
    let currentFile = this.state.selectedFiles[0];

    this.setState({
      currentFile: currentFile,
    });

    UploadService.upload(currentFile,
      (response) => response.json())
      .then(response => {
        this.setState({
          fileInfos: response.data,
        })

      })
      .catch((error) => {
        this.setState({
          erM: error.response.data,
          showModal: true,

        })
      });
    this.setState({
      selectedFiles: undefined,
    });
  }


  render() {
    const {
      selectedFiles,
      fileInfos,
      showModal,
      erM,
      currentUser,
    } = this.state;




    return (
   
      <Container >
        <Modal show={showModal} size="xl" >
          <Modal.Header >
            <Modal.Title>Съобщение за грешка : </Modal.Title>

          </Modal.Header>

          <Modal.Body>
            <p id="demo">Status : {erM.statusCode}</p>
            <p id="demo1">Time Stamp : {erM["timestamp"]}</p>
            <p id="demo2">Message : {erM.message}</p>
            <p id="demo4">Description :  {erM["description"]}</p>
          </Modal.Body>

          <Modal.Footer>
            <Button variant="secondary"
              onClick={this.closeError}>
              Затвори
            </Button>

          </Modal.Footer>
        </Modal>
        <Helmet>
      <style>{'body { background-color: black; }'}</style>
    </Helmet>
    <Navigation />
      
      <Container className="d-flex justify-content-center">
        <label style={{color:"white"}} className="btn btn-default justify-content-center">
          <input type="file"
            onChange={this.selectFile}
            onClick={() => this.setState({ fileInfos: [] })} />
        </label>

        <button
          className="btn btn-dark justify-content-center"
          disabled={!selectedFiles}
          onClick={this.upload}
        >
          Upload
        </button>
        </Container>

        <table className="d-flex justify-content-center">
          <tbody>

          
            <tr>
              <th style={{color:"white"}}>empId1</th>
              <th style={{color:"white"}}>empId2</th>
              <th style={{color:"white"}}>projectId</th>
              <th style={{color:"white"}}>daysWorked</th>

            </tr>

            {fileInfos.map((item, index) => (
              <tr key={index}>
                <td>{item.empId1}</td>
                <td>{item.empId2}</td>
                <td>{item.projectId}</td>
                <td>{item.daysWorked}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </Container>

    );
  }
}
