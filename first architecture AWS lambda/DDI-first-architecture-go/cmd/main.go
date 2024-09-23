package main

import (
	"DDI-first-architecture-go/pkg/injector"
	"log"
	"net/http"
	"time"

	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-lambda-go/lambda"
)

func HandleRequest(request events.APIGatewayProxyRequest) (events.APIGatewayProxyResponse, error) {
	i := injector.GetInstance()
	service := i.GetService("hello")

	start := time.Now().UnixMilli()
	http.Get(service.ServiceAddress)
	end := time.Now().UnixMilli()
	diff := end - start
	log.Printf("hello world function executed in %d ms", diff)

	response := events.APIGatewayProxyResponse{
		StatusCode: 200,
		Body:       "execution completed with success",
	}

	return response, nil
}

func main() {
	lambda.Start(HandleRequest)
}

