Webapp.ApplicationRoute = Ember.Route.extend({
    model: function() {
        return [];
    }
});
Webapp.PlayerRoute = Ember.Route.extend({
    model: function() {
        return {
            name:"Maarten Billemont",
            seniority:new Date(),
            numberOfGames:7,
            victoryPercentage:27,
            badge: Webapp.BadgeView.create({
                primaryColor: "#2F2E2E",
                secondaryColor: "#2BAA9C"
            })
        };
    }
});
