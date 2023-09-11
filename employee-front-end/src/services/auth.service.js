import axios from "axios";

const API_URL = "http://localhost:8083/";

class AuthService {




  async login(username, password) {
    console.log("axios is make request ")




    const response = await axios
      .post(API_URL + "signin", {
        username,
        password,
        // IMPORTANT!!!

        headers: {
         /*  withCredentials: true,  
          "Authorization": 'Bearer ',
         */
          "Access-Control-Allow-Origin": "*",
      }
      }
     
    /*   {withCredentials: "true"} */
      );

     /* Ако има отговор влиза!
     Ако е изписано response.data.JSON  -  не Влиза и го духам.
     */ 
    if (response.data) {
      console.log("axios is make request and hawe response   " + response.request.AuthService)
   
      localStorage.setItem("sesionInfo", JSON.stringify(response.data));
      localStorage.setItem("user", JSON.stringify(response.data.username));
   /*   sessionStorage.setItem("jwt", JSON.stringify(response.data.accessToken));
     sessionStorage.setItem("jwt", JSON.stringify(response.headers.jwt));
     sessionStorage.setItem("token", JSON.stringify(response.headers.username)); */
     console.log();
    }
    console.log(response);
    console.log(response.headers);
    console.log(response.data);
    console.log(document.cookie);
    return response.data;
  }

  logout() {
    localStorage.removeItem("user");
    localStorage.removeItem("username");
 
  }

  register(username, email, password) {
    return axios.post(API_URL + "signup", {
      username,
      email,
      password
    });
  }

  getCurrentUser() {
    return JSON.parse(localStorage.getItem('user'));
  }
}

export default new AuthService();