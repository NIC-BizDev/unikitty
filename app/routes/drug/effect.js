import Ember from 'ember';

export default Ember.Route.extend({
    model: function(params, transition) {
        // Get the UNII from the parent route
        var drug_id = transition.params.drug.drug_id;

        // Fetch the effect model data
        return Ember.RSVP.hash({
            // Gender
            genderCount: $.getJSON('https://api.fda.gov/drug/event.json?search=patient.drug.openfda.unii:' + drug_id + '+AND+patient.reaction.reactionmeddrapt:%22' + params.effect_name + '%22&count=patient.patientsex').then(function (data) {
                var result = {
                    male: {
                        count: 0,
                        pct: 0
                    },
                    female: {
                        count: 0,
                        pct: 0
                    },
                    total: 0
                };

                $.each(data.results, function (key, value) {
                    if (value.term === 1) { // Male
                        result.male.count = value.count;
                    } else if (value.term === 2) { // Female
                        result.female.count = value.count;
                    }
                });

                result.total = result.male.count + result.female.count;
                result.male.pct = Math.round((result.male.count / result.total) * 100);
                result.female.pct = Math.round((result.female.count / result.total) * 100);
                console.log(result);
                return result;
            }),

            chartStub: {
                labels: ['1', '2', '3', '4', '5'],
                series: [
                  [5, 4, 8, 10, 2]
                ]
            },

            chartOptions: {
                showPoint: false,
                height: 200,
                axisX: {
                    // We can disable the grid for this axis
                    showGrid: false
                }
             },

            //Age
            ageCount: $.getJSON('https://api.fda.gov/drug/event.json?search=patient.drug.openfda.unii:' + drug_id + '+AND+patient.reaction.reactionmeddrapt:%22' + params.effect_name + '%22&count=patient.patientonsetage').then(function (data) {

                var result = [];
                var i;
                for (i = 0; i < 110; i++) {
                    result.push({ age: i, count: 0 });
                }

                var tmpAge;
                $.each(data.results, function (key, value) {
                    tmpAge = Math.round(value.term);
                    if(tmpAge > -1 && tmpAge < 110)
                    {
                        result[tmpAge].count += value.count;
                    }
                });

                var chartFormat = {
                       labels: [],
                       series: [[]]
                   };

                   for (i = 0; i < 110; i++) {
                       if((result[i].age % 10)===0){
                       chartFormat.labels.push(result[i].age);
                   }else {
                           chartFormat.labels.push('');
                       }
                       chartFormat.series[0].push(result[i].count);
                   }
                   var results = {results:chartFormat};
                   return results;

              }),

            // Severity/Outcome
            outcomeCount: $.getJSON('https://api.fda.gov/drug/event.json?search=patient.drug.openfda.unii:' + drug_id + '+AND+patient.reaction.reactionmeddrapt:%22' + params.effect_name + '%22&count=patient.reaction.reactionoutcome').then(function (data) {

                var result = {
                    Outcome1: { count: 0, pct: 0 },
                    Outcome2: { count: 0, pct: 0 },
                    Outcome3: { count: 0, pct: 0 },
                    Outcome4: { count: 0, pct: 0 },
                    Outcome5: { count: 0, pct: 0 }
                };

                var total = 0;
                $.each(data.results, function (key, value) {
                    switch(value.term) {
                        case 1:
                            result.Outcome1.count = value.count;
                            break;
                        case 2:
                            result.Outcome2.count = value.count;
                            break;
                        case 3:
                            result.Outcome3.count = value.count;
                            break;
                        case 4:
                            result.Outcome4.count = value.count;
                            break;
                        case 5:
                            result.Outcome5.count = value.count;
                            break;
                        default:
                    }
                    if(value.term > 0 && value.term < 6)
                    {
                        total += value.count;
                    }

                });

                result.Outcome1.pct = (result.Outcome1.count / total)*100;
                result.Outcome2.pct = (result.Outcome2.count / total)*100;
                result.Outcome3.pct = (result.Outcome3.count / total)*100;
                result.Outcome4.pct = (result.Outcome4.count / total)*100;
                result.Outcome5.pct = (result.Outcome5.count / total)*100;

                return result;
              })
        });
    }
});
