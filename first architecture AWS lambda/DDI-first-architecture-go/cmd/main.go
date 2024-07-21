package main

import (
	"DDI-first-architecture-go/pkg/injector"

	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-lambda-go/lambda"
)

func HandleRequest(request events.APIGatewayProxyRequest) (events.APIGatewayProxyResponse, error) {
	i := injector.GetInstance("./services.xml")
	serviceAddress := i.GetService("test")
	var response events.APIGatewayProxyResponse
	if serviceAddress != "" {
		response = events.APIGatewayProxyResponse{
			StatusCode: 200,
			Body:       serviceAddress,
		}

	} else {
		response = events.APIGatewayProxyResponse{
			StatusCode: 404,
			Body:       "Service not found",
		}
	}

	return response, nil
}

func main() {
	lambda.Start(HandleRequest)
}
