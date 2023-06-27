var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');

var indexRouter = require('./routes/index');
var usersRouter = require('./routes/users');

var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', indexRouter);
app.use('/users', usersRouter);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

module.exports = app;

var firebase = require('firebase-admin');
var request = require('request');
var {google} = require('googleapis');

// Fetch the service account key JSON file contents
var key = require("./routes/valisori-72068-firebase-adminsdk-j493d-a82923bdef.json");

var API_KEY = 'AIzaSyCiyx86AFTnFwPkjNnI9CIhbG_iov6HuR8';

// Initialize the app with a service account, granting admin privileges
firebase.initializeApp({
  credential: firebase.credential.cert(key),
  databaseURL: "https://valisori-72068-default-rtdb.firebaseio.com"
});
ref = firebase.database().ref();

const admin = require('firebase-admin');
const natural = require('natural');
const tokenizer = new natural.WordTokenizer();
const Analyzer = natural.SentimentAnalyzer;
const stemmer = natural.PorterStemmer;
const analyzer = new Analyzer("English", stemmer, "afinn");

const colorsDictionary = {
    5: [255, 0, 127],
    2.5: [28, 232, 21],
    0: [255, 233, 0],
    '-2.5': [199, 36, 177],
    '-5': [3, 37, 126]
  };

function associateColor(score) {
    const roundedScore = Object.keys(colorsDictionary).reduce((a, b) => {
        return Math.abs(b - score) < Math.abs(a - score) ? b : a;
      });
      const colorTuple = colorsDictionary[roundedScore];
      return colorTuple;
}

function listenForDataPush() {
    const data = ref.child('data');
    const messageRef = ref.child('colors');
    let tokens;
    let score;
    data.on('child_added', function(requestSnapshot) {
        var text = requestSnapshot.val();
        const { spawn } = require('child_process');
        tokens = tokenizer.tokenize(text);
        score = analyzer.getSentiment(tokens);
        messageRef.set({
            value: associateColor(score)
        }).then(() => {
          console.log('Message pushed to Firebase');
        }).catch((error) => {
          console.error('Error pushing message to Firebase:', error);
        });
    }, function(error) {
      console.error(error);
    });
    
  }
  
  listenForDataPush();
  