import axios from "axios";

// Set backEnd API url

const user = JSON.parse(localStorage.getItem("user"));
console.log("const user axios make employee call "+ user);
let formData = new FormData();
let header = new Headers();
/* const coock = document.cookie.split('; ').filter(row => row.startsWith('jwt')).map(c=>c.split('=')[1])[0]; */
const coock = document.cookie; 
console.log("const cook axios make employee call "+ coock);
const coock1 = document.hasStorageAccess; 
console.log("const hasStorageAccess axios make employee call "+ coock1);
const coock2 = document.token; 
console.log("token axios make employee call "+ coock2);
//header.append("Authorization", token);

/* const token = ("Bearer "+coock); */
/* const token = ("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtaXJvMSIsImlhdCI6MTY5MjE5NTUyNSwiZXhwIjoxNjkyMTk1ODg1fQ.-mUYsKDRY74pZSeP7arf0hnluIVo9T2s_IPaYYUbQPc"); */
export default axios.create({
      // Важно - за да изпрати бисквитката обратно
  baseURL: "http://localhost:8083/",
  
    headers: {
      /* withCredentials: true,  */
    /*   "Authorization": 'Bearer ', */
      "Access-Control-Allow-Origin": "http://localhost:8083/upload",
  }
}
 /*  headers: {
    "Authorization": token,
  } */

);

/* axios
.get("http://localhost:4000/liked-movies", { withCredentials: true })
.then((response) => {
  setMovies(response.data);
});
}, []); */