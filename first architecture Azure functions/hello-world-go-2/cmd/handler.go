package main

import (
	"net/http"
	"os"

	"github.com/gin-gonic/gin"
)

func handler(c *gin.Context) {
	c.IndentedJSON(http.StatusOK, "Hello world")
}

func get_port() string {
	port := ":8080"
	if val, ok := os.LookupEnv("FUNCTIONS_CUSTOMHANDLER_PORT"); ok {
		port = ":" + val
	}
	return port
}

func main() {
	r := gin.Default()
	r.GET("/api/hello-world-go-2", handler)
	r.Run(get_port())
}
