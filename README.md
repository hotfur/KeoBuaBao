# KeoBuaBao

Note that this project use lazy loading information from database, so if you get serialization errors, please fetch them from
database. Hibernate.unproxy will do.

Now if a player quit the room before the game was finished then the game immediately ends and thus spectators and other player
will have no idea what happened -> bad UX. Will fix on.