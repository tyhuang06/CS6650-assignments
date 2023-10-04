module example.com/main

go 1.20

replace example.com/router => ./go

require example.com/router v0.0.0-00010101000000-000000000000

require github.com/gorilla/mux v1.8.0 // indirect
