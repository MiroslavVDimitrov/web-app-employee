import http from "../http-common";

class UploadFilesService {
  // send file to API
  upload(file) { 
  
    let formData = new FormData();
    
    
    formData.append("file", file);
    
   


    return http.post("/upload",formData);
  }


}

export default new UploadFilesService();
