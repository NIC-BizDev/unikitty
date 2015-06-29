import Ember from 'ember';

export function toJson(context) {
  return JSON.stringify(context);
}

export default Ember.HTMLBars.makeBoundHelper(toJson);
