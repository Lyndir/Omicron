Webapp.BadgeView = Ember.View.extend({
    templateName: 'badge',

    primaryStyle: function() {
        return sprintf("background-color: %1$s; box-shadow: inset 0 0 5px %1$s;",
                       this.get('primaryColor'));
    }.property('primaryColor'),
    secondaryStyle: function() {
        return sprintf("background-color: %1$s; box-shadow: 0 0 15px %1$s;",
                       this.get('secondaryColor'));
    }.property('secondaryColor')
});
