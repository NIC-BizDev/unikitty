import Ember from 'ember';

export default Ember.Component.extend({

  classNames: ['autocomplete-search'],
  large: false,
  didInsertElement: function(){
    $('.drug-search').autocomplete({
      minLength: 2,
      source: function (request, response) {
        $.ajax({
          type: 'GET',
          dataType:'json',
          url: 'autocomplete?name=' + $('.drug-search').val(),
          data: request.value,
          error: function (xhr) {
            console.log('Error: ' + xhr.responseText);
          },
          success: function (data) {
            response($.map(data, function (item) {
              return item;
            }));
          }
        });
      },
      focus: function(event, ui) {
        event.preventDefault();
        $(this).val(ui.item.label);
      },
      select: function(event, ui) {
        event.preventDefault();
        $(this).val(ui.item.label);
        $(this).submit();
        return false;
      }
    });
  },
  actions: {
    sendSearch: function() {
      var keyword = $('.drug-search').val().replace(/ /g,"+");
      console.log("sending search. keyword=" + keyword);
      this.sendAction('action', keyword);
    }
  }
});
