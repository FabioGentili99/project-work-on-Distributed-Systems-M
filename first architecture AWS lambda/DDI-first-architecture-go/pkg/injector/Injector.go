package injector

import (
	"fmt"
	"log"
	"sync"
	"time"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/dynamodb"
	"github.com/aws/aws-sdk-go/service/dynamodb/dynamodbattribute"
)

const tableName = "service-registry2"

type Injector struct {
	connection *dynamodb.DynamoDB
}

type Service struct {
	id             string `dynamodbav:"id"`
	ServiceName    string `dynamodbav:"ServiceName"`
	ServiceAddress string `dynamodbav:"ServiceAddress"`
}

var instance *Injector
var once sync.Once

func GetInstance() *Injector {
	once.Do(func() {
		sess := session.Must(session.NewSessionWithOptions(session.Options{
			SharedConfigState: session.SharedConfigEnable,
		}))
		i := &Injector{
			connection: dynamodb.New(sess),
		}
		instance = i
	})

	return instance
}

func (i *Injector) RegisterService(id, serviceName, serviceURL string) {
	_, err := i.connection.PutItem(&dynamodb.PutItemInput{
		TableName: aws.String(tableName),
		Item: map[string]*dynamodb.AttributeValue{
			"id":             {S: aws.String(id)},
			"ServiceName":    {S: aws.String(serviceName)},
			"ServiceAddress": {S: aws.String(serviceURL)},
		},
	})
	if err != nil {
		fmt.Printf("error during service registration")
	}
}

func (i *Injector) GetService(id string) Service {
	start := time.Now().UnixMilli()
	result, _ := i.connection.GetItem(&dynamodb.GetItemInput{
		TableName: aws.String(tableName),
		Key: map[string]*dynamodb.AttributeValue{
			"id": {S: aws.String(id)},
		},
	})
	end := time.Now().UnixMilli()

	log.Printf("Read from DynamoDB table executed in %d ms", (end - start))

	service := Service{}
	dynamodbattribute.UnmarshalMap(result.Item, &service)

	return service
}

