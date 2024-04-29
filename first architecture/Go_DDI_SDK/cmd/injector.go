package cmd

import (
	"encoding/xml"
	"fmt"
	"io/ioutil"
	"net/http"
	"strings"
)

type Injector struct {
	RegistryBaseUrl string
	ConfigFile      string
}

type Services struct {
	XMLName xml.Name  `xml:"services"`
	Service []Service `xml:"service"`
}

type Service struct {
	XMLName xml.Name `xml:"service"`
	Name    string   `xml:"name"`
	Address string   `xml:"address"`
}

func NewInjector(configFile string) *Injector {
	injector := &Injector{
		RegistryBaseUrl: "http://localhost:8080",
		ConfigFile:      configFile,
	}

	if injector.ConfigFile != "" {
		data, err := ioutil.ReadFile(injector.ConfigFile)
		if err != nil {
			fmt.Println(err)
			return injector
		}

		var services Services
		err = xml.Unmarshal(data, &services)
		if err != nil {
			fmt.Println(err)
			return injector
		}

		for _, service := range services.Service {
			injector.RegisterService(service.Name, service.Address)
		}
	}

	return injector
}

func (i *Injector) RegisterService(serviceName, serviceUrl string) {
	endpoint := fmt.Sprintf("%s/register/%s", i.RegistryBaseUrl, serviceName)
	client := &http.Client{}
	req, err := http.NewRequest("POST", endpoint, strings.NewReader(serviceUrl))
	if err != nil {
		fmt.Printf("Failed to register service %s: %s\n", serviceName, err.Error())
		return
	}

	req.Header.Set("Content-Type", "text/plain")

	resp, err := client.Do(req)
	if err != nil {
		fmt.Printf("Failed to register service %s: %s\n", serviceName, err.Error())
		return
	}
	defer resp.Body.Close()

	if resp.StatusCode == http.StatusOK {
		fmt.Printf("Service %s registered successfully.\n", serviceName)
	} else {
		fmt.Printf("Failed to register service %s: %s\n", serviceName, resp.Status)
	}
}

func (i *Injector) GetService(serviceName string) string {
	endpoint := fmt.Sprintf("%s/discover/%s", i.RegistryBaseUrl, serviceName)
	resp, err := http.Get(endpoint)
	if err != nil {
		fmt.Printf("Failed to discover service %s: %s\n", serviceName, err.Error())
		return ""
	}
	defer resp.Body.Close()

	if resp.StatusCode == http.StatusOK {
		body, err := ioutil.ReadAll(resp.Body)
		if err != nil {
			fmt.Printf("Failed to read response for service %s: %s\n", serviceName, err.Error())
			return ""
		}
		serviceUrl := string(body)
		fmt.Printf("Discovered service %s at %s\n", serviceName, serviceUrl)
		return serviceUrl
	} else {
		fmt.Printf("Failed to discover service %s: %s\n", serviceName, resp.Status)
		return ""
	}
}
