// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
/**
 * Adds a random fact to the page.
 * This is just a modification from random greeting
 */
function addRandomFact() {
  const facts = [
    'I joined Google during the Covid crisis',
    'Most people call me Sofi!',
    'My favorite non fiction book is The Code Book, by Simon Singh',
    'I\'m 1.5 meters tall.',
    'I have two younger brothers',
  ];
  const fact = facts[Math.floor(Math.random() * facts.length)];
  const factContainer = document.getElementById('fact-container');
  factContainer.innerText = fact;
}

/** Calls for DeleteCommentsServer, then reloads the window */
function deleteAllComments() {
  fetch('/delete-data', {method: 'POST'} ).then(window.location.reload());
}

/** Adds all comments and usernames to the 'comments' division. */
function getCommentsJson() {
  fetch('/data').then(response => response.json()).then((comment) => {
    const containerElement=document.getElementById('comments');
    let i;
    for (i=0; i<comment.array.length; i++) {
      containerElement.appendChild(createSpanElement(comment.array[i].username));
      containerElement.appendChild(createSpanElement(comment.array[i].email));
      containerElement.appendChild(createParagraphElement(comment.array[i].text));
    }
  });
}

/**
 * Creates a new paragraph element in document
 * @param {string} text Inner text for the new element
 * @return {pElement} The new <p> element
 */
function createParagraphElement(text) {
  const pElement = document.createElement('P');
  pElement.innerText = text;
  return pElement;
}

/**
 * Creates a new span element in document
 * @param {string} text Inner text for the new element
 * @return {spanElement} The new <span> element
 */
function createSpanElement(text) {
  const spanElement = document.createElement('span');
  spanElement.innerText = text;
  return spanElement;
}

/** Fetches Json with the login/logout URL*/
function getLoginJson() {
  fetch('/login').then(response => response.json()).then((logJson) => {
    createLogButton(logJson);
  });
}

/**
 * Creates login/logout button, hides comment-form if user is logged out
 * @param {json} logJson Json fetched from LoginServlet
 */
function createLogButton(logJson) {
  const containerElement = document.getElementById('login');
  const commentForm = document.getElementById('comment-form');
  const loginlink = document.createElement('a');
  const add = document.getElementById('add');

  if (logJson.logged == 'out') {
    commentForm.style.display = 'none';
    loginlink.innerText = 'Click here to log in';
    add.innerText = 'Log in to add a comment!';
  } else {
    commentForm.style.display = 'block';
    loginlink.innerText = 'Click here to log out';
  }

  loginlink.id = 'login-button';
  loginlink.href = logJson.url;
  containerElement.appendChild(loginlink);
}

/** Gets latest comments and login/logout button when index.html loads*/
function start() {
  getCommentsJson();
  getLoginJson();
  initMap();
}

let editMarker;
let map;

/** Creates a google maps object in 'map' div */
function initMap() {
  let culiacan = {lat: 24.8, lng: -107.39};
  map = new google.maps.Map(document.getElementById('map'), {
    center: culiacan,
    zoom: 8,
  });
  let marker = new google.maps.Marker({position: culiacan, map: map});
  map.addListener('click', (event) => {
    createMarker(event.latLng.lat(), event.latLng.lng());
  });
  fetchMarkers();
}

/**
 * Creates a new marker in the map and stores it in the servlet
 * @param {number} lat Latitude of the marker
 * @param {number} lng Longitude of the marker
 */
function createMarker(lat, lng) {
  editMarker = new google.maps.Marker( {position: {lat: lat, lng: lng}, map: map});
  postMarker(lat, lng);
}

/**
 * Sends a post request to the MarkerServlet to store the new marker
 * @param {number} lat Latitude of the marker
 * @param {number} lng Longitude of the marker
 */
function postMarker(lat, lng) {
  const params = new URLSearchParams();
  params.append('lat', lat);
  params.append('lng', lng);

  fetch('/markers', {method: 'POST', body: params});
}

/** Fetches all marker entities from the MarkerServlet and creates all markers on the map */
function fetchMarkers() {
  fetch('/markers').then(response => response.json()).then((markers) => {
    let i;
    for (i=0; i<markers.array.length; i++) {
      const newMarker = new google.maps.Marker( {position: {lat: markers.array[i].lat, lng: markers.array[i].lng}, map: map});
    }
  });
}
