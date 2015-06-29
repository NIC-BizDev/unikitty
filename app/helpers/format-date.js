import Ember from 'ember';

export function formatDate(date) {
  var dataFormat1 = "YYYYDDMM";
  var dataFormat2 = "YYYYMMDD";
  var validDate = "";
  if(moment(date, dataFormat1).isValid()){
    validDate = moment(date, dataFormat1).format("MM/DD/YYYY");
  } else if(moment(date, dataFormat2).isValid()){
    validDate = moment(date, dataFormat2).format("MM/DD/YYYY");
  }
  return validDate;
}

export default Ember.HTMLBars.makeBoundHelper(formatDate);
