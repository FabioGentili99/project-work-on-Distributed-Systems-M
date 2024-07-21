package injector

import (
	"encoding/xml"
	"fmt"
	"io/ioutil"
	"log"
	"sync"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/dynamodb"
	"github.com/aws/aws-sdk-go/service/dynamodb/dynamodbattribute"
)

const tableName = "service-registry"

type Injector struct {
	//registry   map[string]string
	connection *dynamodb.DynamoDB
	configFile string
}

var instance *Injector
var once sync.Once

/*func getInstance(configFile string) *Injector {
	if instance == nil {
		i := &Injector{
			registry:   make(map[string]string),
			configFile: configFile,
		}

		i.loadConfig()
		instance = i
		return i
	} else {
		return instance
	}
}*/

func GetInstance(configFile string) *Injector {
	once.Do(func() {
		sess := session.Must(session.NewSessionWithOptions(session.Options{
			SharedConfigState: session.SharedConfigEnable,
		}))
		i := &Injector{
			//registry:   make(map[string]string),
			connection: dynamodb.New(sess),
			configFile: configFile,
		}
		i.loadConfig()
		instance = i
	})

	return instance
}

func (i *Injector) loadConfig() {
	data, err := ioutil.ReadFile(i.configFile)
	if err != nil {
		log.Printf("Error reading config file: %v", err)
		return
	}

	var services struct {
		Service []struct {
			Name    string `xml:"name"`
			Address string `xml:"address"`
		} `xml:"service"`
	}

	err = xml.Unmarshal(data, &services)
	if err != nil {
		log.Printf("Error parsing config file: %v", err)
		return
	}

	for _, service := range services.Service {
		i.RegisterService(service.Name, service.Address)
	}
}

func (i *Injector) RegisterService(serviceName, serviceURL string) {
	//i.registry[serviceName] = serviceURL
	//fmt.Printf("Service %s registered successfully.\n", serviceName)
	_, err := i.connection.PutItem(&dynamodb.PutItemInput{
		TableName: aws.String(tableName),
		Item: map[string]*dynamodb.AttributeValue{
			"ServiceName":    {S: aws.String(serviceName)},
			"ServiceAddress": {S: aws.String(serviceURL)},
		},
	})
	if err != nil {
		fmt.Printf("error during service registration")
	}
}

func (i *Injector) GetService(serviceName string) string {
	/*url, ok := i.registry[serviceName]
	if !ok {
		fmt.Printf("Failed to discover service %s\n", serviceName)
		return ""
	}
	return url*/
	result, err := i.connection.GetItem(&dynamodb.GetItemInput{
		TableName: aws.String(tableName),
		Key: map[string]*dynamodb.AttributeValue{
			"ServiceName": {S: aws.String(serviceName)},
		},
	})
	if err != nil {
		fmt.Printf("error during service retrieving")
		return ""
	}

	var service struct {
		ServiceAddress string `dynamodbav:"ServiceAddress"`
	}
	err = dynamodbattribute.UnmarshalMap(result.Item, &service)
	if err != nil {
		fmt.Printf("error unmarshaling service")
		return ""
	}

	return service.ServiceAddress
}
