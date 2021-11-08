package main

import (
	"database/sql"
	"fmt"
	_ "github.com/go-sql-driver/mysql"
	"github.com/gorilla/mux"
	"github.com/gorilla/websocket"
	"html/template"
	"log"
	"math/rand"
	"net/http"
	"time"

	"github.com/go-echarts/go-echarts/v2/charts"
	"github.com/go-echarts/go-echarts/v2/opts"
	"github.com/go-echarts/go-echarts/v2/types"
)
// https://gowebexamples.com/
func main() {
	println("start web")

	D8()
}

func D8(){
	http.HandleFunc("/", draw)

	http.ListenAndServe(":8080", nil)
}

// generate random data for line chart
func generateLineItems() []opts.LineData {
	items := make([]opts.LineData, 0)
	for i := 0; i < 7; i++ {
		items = append(items, opts.LineData{Value: rand.Intn(300)})
	}
	return items
}

func draw(w http.ResponseWriter, _ *http.Request) {
	// create a new line instance
	line := charts.NewLine()
	// set some global options like Title/Legend/ToolTip or anything else
	line.SetGlobalOptions(
		charts.WithInitializationOpts(opts.Initialization{Theme: types.ThemeWesteros}),
		charts.WithTitleOpts(opts.Title{
			Title:    "Line example in Westeros theme",
			Subtitle: "Line chart rendered by the http server this time",
		}))

	// Put data into instance
	line.SetXAxis([]string{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"}).
		AddSeries("Category A", generateLineItems()).
		AddSeries("Category B", generateLineItems()).
		SetSeriesOptions(charts.WithLineChartOpts(opts.LineChart{Smooth: true}))
	line.Render(w)
}

func D7() {
	// WebSocket
	// https://github.com/gorilla/websocket/tree/master/examples/echo
	http.HandleFunc("/echo", func(w http.ResponseWriter, r *http.Request) {
		conn, _ := upgrader.Upgrade(w, r, nil)

		for {
			msgType, msg, err := conn.ReadMessage()
			if err != nil {
				fmt.Println("read error")
				return
			}

			fmt.Printf("%s send: %s\n", conn.RemoteAddr(), string(msg))

			if err = conn.WriteMessage(msgType, []byte("ack>"+string(msg))); err != nil {
				fmt.Println("write error")
				return
			}
		}
	})
	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		http.ServeFile(w, r, "template/websockets.html")
	})

	http.ListenAndServe(":8080", nil)
}

var upgrader = websocket.Upgrader{
	ReadBufferSize:  1024,
	WriteBufferSize: 1024,
}

func D6() {
	// Mat Ryers Building APIs chains
	// https://gowebexamples.com/advanced-middleware/
}

func D5() {
	// 中间件
	http.HandleFunc("/foo", logging(foo))
	http.ListenAndServe(":80", nil)
}
func foo(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w, "foo")
}

func logging(f http.HandlerFunc) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		log.Println(r.URL.Path)
		f(w, r)
	}
}

func D4() {
	// Input HTML
	// https://developer.mozilla.org/zh-CN/docs/Web/HTML/Element/Input
	tmpl := template.Must(template.ParseFiles("template/forms.html"))
	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {

		defaultDetail := ContactDetails{
			Email:   "yinweiwen@126.com",
			Subject: "software engineer",
			Message: "",
		}
		_ = defaultDetail
		if r.Method != http.MethodPost {
			tmpl.Execute(w, nil)
			return
		}
		details := ContactDetails{
			Email:   r.FormValue("email"),
			Subject: r.FormValue("subject"),
			Message: r.FormValue("message"),
		}

		fmt.Printf("%v", details)

		tmpl.Execute(w, struct {
			Success bool
		}{true})
	})
	http.ListenAndServe(":80", nil)
}

func D3() {
	tmpl := template.Must(template.ParseFiles("template/layout.html"))
	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		data := struct {
			PageTitle string
			Todos     []struct {
				Title string
				Done  bool
			}
		}{PageTitle: "thistile",
			Todos: []struct {
				Title string
				Done  bool
			}{{Title: "a", Done: true}, {Title: "b", Done: false}}}
		tmpl.Execute(w, data)
	})
	http.ListenAndServe(":80", nil)
}

func D2() {
	// 处理复杂路由请求
	r := mux.NewRouter()
	r.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		fmt.Fprintf(w, "hello")
	})
	r.HandleFunc("/book/{name}/page/{page}", func(w http.ResponseWriter, r *http.Request) {
		vars := mux.Vars(r)
		fmt.Fprintf(w, "book:%s page:%s", vars["name"], vars["page"])
	})

	// r.HandleFunc("/books/{title}", CreateBook).Methods("POST")
	//bookrouter := r.PathPrefix("/books").Subrouter()
	//bookrouter.HandleFunc("/", AllBooks)
	//bookrouter.HandleFunc("/{title}", GetBook)

	http.ListenAndServe(":80", r)
}

func D1() {
	// GET parameters r.URL.Query().Get("token")
	// POST parameters r.FormValue("email").
	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		fmt.Fprintf(w, "Hello you are visited %s", r.URL.Path)
	})

	// File Server
	fs := http.FileServer(http.Dir("static/"))
	http.Handle("/static/", http.StripPrefix("/static/", fs))

	http.ListenAndServe(":80", nil)
}

/*
```sql
create database test;

CREATE TABLE users (
    id INT AUTO_INCREMENT,
    username TEXT NOT NULL,
    password TEXT NOT NULL,
    created_at DATETIME,
    PRIMARY KEY (id)
);
```
*/
func S1() {
	db, err := sql.Open("mysql", "root:123456@(127.0.0.1:3306)/test?parseTime=true")
	if err != nil {
		log.Fatal(err)
	}
	if err := db.Ping(); err != nil {
		log.Fatal(err)
	}

	// insert
	{
		username := "yinweiwen"
		pasword := "poi373"
		createAt := time.Now()

		result, err := db.Exec(`
insert into users (username,password,created_at) values (?,?,?)
`, username, pasword, createAt)
		if err != nil {
			log.Fatal(err)
		}

		id, err := result.LastInsertId()
		fmt.Println(id)
	}

	// query
	{
		var (
			id       int
			username string
			password string
			createAt time.Time
		)

		err := db.QueryRow(`select id, username, password, created_at from users where id=?`, 1).Scan(&id, &username, &password, &createAt)
		if err != nil {
			log.Fatal(err)
		}
		fmt.Println("query 1:")
		fmt.Println(id, username, password, createAt)
	}
	// query all
	{
		type user struct {
			id       int
			username string
			password string
			createAt time.Time
		}
		rows, err := db.Query(`SELECT id, username, password, created_at FROM users`)
		if err != nil {
			log.Fatal(err)
		}
		defer rows.Close()

		var users []user
		for rows.Next() {
			var u user

			err := rows.Scan(&u.id, &u.username, &u.password, &u.createAt)
			if err != nil {
				log.Fatal(err)
			}
			users = append(users, u)
		}
		if err := rows.Err(); err != nil {
			log.Fatal(err)
		}

		fmt.Printf("%#v", users)
	}
}

type ContactDetails struct {
	Email   string
	Subject string
	Message string
}
