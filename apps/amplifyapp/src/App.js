import React, { useState } from 'react';
import useInterval from './useInterval';
import logo from './logo.svg';
import './App.css';

function getRandomColor() {
  let colorValues = ["red", "blue", "green"];
  return colorValues[Math.floor(Math.random() * colorValues.length)];
}

let API_URL = "https://35154wubcc.execute-api.us-east-1.amazonaws.com"

function App() {
  const [currentTime, setCurrentTime] = useState('01/01/1970, 00:00:00');
  const [currentRandomNumber, setCurrentRandomNumber] = useState('-1');
  const [currentRandomString, setCurrentRandomString] = useState('<null>');
  const [quoteText, setQuoteText] = useState('To be or not to be.');
  const [quoteAuthor, setQuoteAuthor] = useState('William Shakespeare');

  const delay = 5000; // in milliseconds
  const quoteDelay = 3000; // in milliseconds

App.refreshTime = () => {
    console.log("Refreshing ... time ...");
    // fetch('/api/time')
    fetch(API_URL+'/api/time')
      .then(response => response.json())
      .then(data => {
        setCurrentTime(data.datetime);
        console.log(data);
    });
    console.log("Refreshed time.");  
  }
  useInterval(() => {
    App.refreshTime();
  }, delay);

  App.refreshRandomNumber = () => {
    console.log("Refreshing ... random number ...");
    // fetch('/random/number')
    fetch(API_URL+'/random/number')
      .then(response => response.json())
      .then(data => {
        setCurrentRandomNumber(data.random_number);
        console.log(data);
    });
    console.log("Refreshed random number.");  
  }
  useInterval(() => {
    App.refreshRandomNumber();
  }, delay);

  App.refreshQuote = () => {
    console.log("Refreshing ... quote ...");
    fetch(API_URL+'/random/quote')
      .then(response => response.json())
      .then(data => {
        setQuoteText(data.random_quote);
        setQuoteAuthor(data.quote_author);
        console.log(data);
    });
    console.log("Refreshed quote.");  
  }
  useInterval(() => {
    App.refreshQuote();
  }, quoteDelay);

    App.refreshRandomString = () => {
    console.log("Refreshing ... random string ...");
    fetch(API_URL+'/random/string')
      .then(response => response.json())
      .then(data => {
        setCurrentRandomString(data.random_string);
        console.log(data);
    });
    console.log("Refreshed random string.");  
  }
  useInterval(() => {
    App.refreshRandomString();
  }, delay);

  App.buttonClicked = () => {
    console.log('Button was clicked!');
    App.refreshTime();
    App.refreshRandomNumber();
    App.refreshRandomString();
    App.refreshQuote();
  }

  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="react-logo" />
        <a
          className="App-link"
          href="https://github.com/robmarano/ece465_at_cooper/tree/Session_13/apps/amplifyapp"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn how to spin React!
        </a>
        <button className="button" onClick={App.buttonClicked}>Click to Refresh</button>
        <p>The current time is</p>
        <div style={{background: `${getRandomColor()}`}}>
          {currentTime}
        </div>
        <p>The current random number is</p>
        <div style={{background: `${getRandomColor()}`}}>
          {currentRandomNumber}
        </div>
        <p>The current random string is</p>
        <div style={{background: `${getRandomColor()}`}}>
          {currentRandomString}
        </div>
        <p>The current quote is</p>
        <div>
          <blockquote>
          <p>{quoteText}</p>
          <footer>— <cite>{quoteAuthor}</cite>
          </footer>
          </blockquote>
        </div>
        <p></p>
        <p></p>
        <p></p>
      </header>
    </div>
  );

}

export default App;
