package injector

import (
	"encoding/xml"
	"fmt"
	"io/ioutil"
	"log"
	"sync"
)

type Injector struct {
	registry   map[string]string
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
		i := &Injector{
			registry:   make(map[string]string),
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
	i.registry[serviceName] = serviceURL
	fmt.Printf("Service %s registered successfully.\n", serviceName)
}

func (i *Injector) GetService(serviceName string) string {
	url, ok := i.registry[serviceName]
	if !ok {
		fmt.Printf("Failed to discover service %s\n", serviceName)
		return ""
	}
	return url
}
