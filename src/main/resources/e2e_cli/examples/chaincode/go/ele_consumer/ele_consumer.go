package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"strconv"
	"regexp"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	sc "github.com/hyperledger/fabric/protos/peer"
)

type SmartContract struct {
}

type EnergyConsumer struct {
	MeterId     string `json:"meterId"`
	Electricity string `json:"electricity"`
	StartTime   string `json:"startTime"`
	EndTime     string `json:"endTime"`
}

func (s *SmartContract) Init(stub shim.ChaincodeStubInterface) sc.Response {
	return shim.Success(nil)
}

func (s *SmartContract) Invoke(stub shim.ChaincodeStubInterface) sc.Response {
	function, args := stub.GetFunctionAndParameters()
	if function == "initTest" {
		return s.initTestEnergyConsumer(stub)
	} else if function == "invoke" {
		return s.invoke(stub, args)
	} else if function == "query" {
		return s.query(stub, args)
	} else if function == "queryAll" {
		return s.queryAll(stub, args)
	}
	return shim.Error("Invalid Smart Contract function name. Expecting <initTest> / <invoke> / <query> / <queryAll>")
}

func (s *SmartContract) initTestEnergyConsumer(stub shim.ChaincodeStubInterface) sc.Response {
	EnergyConsumers := []EnergyConsumer{
		{MeterId: "a09633f5-961e-4ad7-9d63-19d58349bda0", Electricity: "1.5", StartTime: "1527238481", EndTime: "1527238846"},
		{MeterId: "7d21b0a5-e829-44fb-a8d6-3e174bdc9518", Electricity: "102.1", StartTime: "1527238482", EndTime: "1527238847"},
		{MeterId: "61a5d5c1-33c3-4eb4-a600-8c9e2a630d72", Electricity: "30.2", StartTime: "1527238483", EndTime: "1527238848"},
		{MeterId: "2e5a3658-69f8-4dd7-92b7-af3c856f5042", Electricity: "25.8", StartTime: "1527238484", EndTime: "1527238849"},
	}
	i := 0
	for i < len(EnergyConsumers) {
		fmt.Println("i is ", i)
		energyConsumerAsBytes, _ := json.Marshal(EnergyConsumers[i])
		stub.PutState("EC"+strconv.Itoa(i), energyConsumerAsBytes)
		fmt.Println("Added", EnergyConsumers[i])
		i = i + 1
	}
	return shim.Success(nil)
}

func (s *SmartContract) invoke(stub shim.ChaincodeStubInterface, args []string) sc.Response {
	if len(args) != 5 {
		return shim.Error("Incorrect number of arguments. Expecting 5. Not " + strconv.Itoa(len(args)))
	}
	keyFlag, _ := regexp.MatchString("^\\d+$", args[0])
	if !keyFlag {
		return shim.Error("Incorrect Key of arguments. Expecting number. Not " + args[0])
	}
	idFlag, _ := regexp.MatchString("^[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}$", args[1])
	if !idFlag {
		return shim.Error("Incorrect MeterId of arguments. Expecting uuid. Not " + args[1])
	}
	eleFlag, _ := regexp.MatchString("^\\d+(\\.\\d{1,2})?$", args[2])
	if !eleFlag {
		return shim.Error("Incorrect Electricity of arguments. Expecting [^\\d+(\\.\\d{1,2})?$]. Not " + args[2])
	}
	sTimeFlag, _ := regexp.MatchString("^\\d{10}$", args[3])
	if !sTimeFlag {
		return shim.Error("Incorrect StartTime of arguments. Expecting [^\\d{10}$]. Not " + args[3])
	}
	eTimeFlag, _ := regexp.MatchString("^\\d{10}$", args[4])
	if !eTimeFlag {
		return shim.Error("Incorrect EndTime of arguments. Expecting [^\\d{10}$]. Not " + args[4])
	}

	var energyConsumer = EnergyConsumer{MeterId: args[1], Electricity: args[2], StartTime: args[3], EndTime: args[4]}
	fmt.Println("New Added:", energyConsumer)

	energyConsumerAsBytes, _ := json.Marshal(energyConsumer)
	fmt.Println("New args[0]:", "EC"+args[0])
	fmt.Println("New EnergyConsumerAsBytes:", energyConsumerAsBytes)

	stub.PutState("EC"+args[0], energyConsumerAsBytes)

	return shim.Success(nil)
}

func (s *SmartContract) query(stub shim.ChaincodeStubInterface, args []string) sc.Response {
	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting 1. Not " + strconv.Itoa(len(args)))
	}
	energyConsumerAsBytes, _ := stub.GetState("EC"+args[0])
	return shim.Success(energyConsumerAsBytes)
}

func (s *SmartContract) queryAll(stub shim.ChaincodeStubInterface, args []string) sc.Response {
	startKey := "EC0"
	endKey := "EC999999999"
	if len(args) == 1 {
		end, _ := regexp.MatchString("^\\d+$", args[0])
		if !end {
			return shim.Error("Incorrect EndKey of arguments. Expecting number. Not " + args[0])
		}
		endKey = "EC" + args[0]
	} else if len(args) == 2 {
		start, _ := regexp.MatchString("^\\d+$", args[0])
		if !start {
			return shim.Error("Incorrect StartKey of arguments. Expecting number. Not " + args[0])
		}
		end, _ := regexp.MatchString("^\\d+$", args[1])
		if !end {
			return shim.Error("Incorrect EndKey of arguments. Expecting number. Not " + args[1])
		}
		sInt, _ := strconv.Atoi(args[0])
		eInt, _ := strconv.Atoi(args[1])
		if sInt > eInt {
			return shim.Error("Incorrect StartKey and EndKey of arguments. Expecting StartKey less than EndKey")
		}
		startKey = "EC" + args[0]
		endKey = "EC" + args[1]
	} else if len(args) != 0 {
		return shim.Error("Incorrect number of arguments. Expecting 0-2. Not " + strconv.Itoa(len(args)))
	}

	resultsIterator, err := stub.GetStateByRange(startKey, endKey)
	if err != nil {
		return shim.Error(err.Error())
	}
	defer resultsIterator.Close()

	var buffer bytes.Buffer
	buffer.WriteString("[")

	bArrayMemberAlreadyWritten := false
	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		if err != nil {
			return shim.Error(err.Error())
		}

		if bArrayMemberAlreadyWritten == true {
			buffer.WriteString(",")
		}
		buffer.WriteString("{\"key\":")
		buffer.WriteString("\"")
		buffer.WriteString(queryResponse.Key)
		buffer.WriteString("\"")

		buffer.WriteString(", \"value\":")

		buffer.WriteString(string(queryResponse.Value))
		buffer.WriteString("}")
		bArrayMemberAlreadyWritten = true
	}
	buffer.WriteString("]")

	fmt.Printf("- listAllEnergyConsumer:\n%s\n", buffer.String())

	return shim.Success(buffer.Bytes())
}

func main() {
	err := shim.Start(new(SmartContract))
	if err != nil {
		fmt.Printf("Error creating new Smart Contract: %s", err)
	}
}
