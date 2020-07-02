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

/** Passes the response into handleResponse() once request is complete */
function getGreeting() {
  const responsePromise = fetch('/data');
  responsePromise.then(handleResponse);
}

/** Converts response to text and passes the result into addGreetingToDom */
function handleResponse(response) {
  const textPromise = response.text();
  textPromise.then(addGreetingToDom);
}

/** Adds a random quote to the DOM. */
function addGreetingToDom(greeting) {
  const greetingContainer = document.getElementById('greeting');
  greetingContainer.innerText = greeting;
}

/** Adds 3 first comments to the 'greetings' division. */
function getJson() {
  fetch('/data').then(response => response.json()).then((greeting) => {
    const containerElement=document.getElementById('greeting');
    containerElement.appendChild(createParagraphElement(greeting[0]));
    containerElement.appendChild(createParagraphElement(greeting[1]));
    containerElement.appendChild(createParagraphElement(greeting[2]));
  });
}

/** Creates a new paragraph element in document */
function createParagraphElement(text) {
  const pElement = document.createElement('P');
  pElement.innerText = text;
  return pElement;
}
