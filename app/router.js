import Ember from 'ember';
import config from './config/environment';

var Router = Ember.Router.extend({
  location: config.locationType
});

Router.map(function() {
  this.route('dashboard', {path: "/"});

  this.route('drug', {path: "/drug/:drug_id"}, function() {
    this.route('effect', {path: ":effect_name"});
  });

  this.route('search', function() {
    this.route('results', {path: ':keyword'});
  });
  this.route('my-meds', function() {
    this.route('notifications');
  });

  this.route('style-guide');
  this.route('about-myfda');
});

Router.reopen({
  notifyGoogleAnalytics: function() {
    if (window._gaq === undefined) {
      return;
    }
    Ember.run.next(function () {
      window._gaq.push(['_trackPageview', window.location.hash.substr(1)]);
    });

  }.on('didTransition')
});

export default Router;
