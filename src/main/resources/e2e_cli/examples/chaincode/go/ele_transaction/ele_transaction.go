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

type TransactionLedger struct {
	CompanyId   string `json:"companyId"`
	MeterId     string `json:"meterId"`
	Electricity string `json:"electricity"`
	Account     string `json:"account"`
	TradingTime string `json:"tradingTime"`
}

func (s *SmartContract) Init(stub shim.ChaincodeStubInterface) sc.Response {
	return shim.Success(nil)
}

func (s *SmartContract) Invoke(stub shim.ChaincodeStubInterface) sc.Response {
	function, args := stub.GetFunctionAndParameters()
	if function == "initTest" {
		return s.initTestTransactionLedger(stub)
	} else if function == "invoke" {
		return s.invoke(stub, args)
	} else if function == "query" {
		return s.query(stub, args)
	} else if function == "queryAll" {
		return s.queryAll(stub, args)
	}
	return shim.Error("Invalid Smart Contract function name. Expecting <initTest> / <invoke> / <query> / <queryAll>")
}

func (s *SmartContract) initTestTransactionLedger(stub shim.ChaincodeStubInterface) sc.Response {
	TransactionLedgers := []TransactionLedger{
		{CompanyId: "a09633f5-961e-4ad7-ad63-19d58349bda0", MeterId: "a09633g5-961e-4ad7-9d63-19d58349bda0", Electricity: "1.5", Account: "15", TradingTime: "1527238846"},
		{CompanyId: "7d21b0a5-e829-44fb-b8d6-3e174bdc9518", MeterId: "7d21b0b5-e829-44fb-a8d6-3e174bdc9518", Electricity: "102.1", Account: "1021", TradingTime: "1527238847"},
		{CompanyId: "61a5d5c1-33c3-4eb4-b600-8c9e2a630d72", MeterId: "61a5d5d1-33c3-4eb4-a600-8c9e2a630d72", Electricity: "30.2", Account: "302", TradingTime: "1527238848"},
		{CompanyId: "2e5a3658-69f8-4dd7-02b7-af3c856f5042", MeterId: "2e5a3668-69f8-4dd7-92b7-af3c856f5042", Electricity: "25.8", Account: "258", TradingTime: "1527238849"},
	}
	i := 0
	for i < len(TransactionLedgers) {
		fmt.Println("i is ", i)
		transactionLedgerAsBytes, _ := json.Marshal(TransactionLedgers[i])
		stub.PutState("TL"+strconv.Itoa(i), transactionLedgerAsBytes)
		fmt.Println("Added", TransactionLedgers[i])
		i = i + 1
	}
	return shim.Success(nil)
}

func (s *SmartContract) invoke(stub shim.ChaincodeStubInterface, args []string) sc.Response {
	if len(args) != 6 {
		return shim.Error("Incorrect number of arguments. Expecting 6. Not " + strconv.Itoa(len(args)))
	}
	keyFlag, _ := regexp.MatchString("^\\d+$", args[0])
	if !keyFlag {
		return shim.Error("Incorrect Key of arguments. Expecting number. Not " + args[0])
	}
	companyIdFlag, _ := regexp.MatchString("^[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}$", args[1])
	if !companyIdFlag {
		return shim.Error("Incorrect CompanyId of arguments. Expecting uuid. Not " + args[1])
	}
	meterIdFlag, _ := regexp.MatchString("^[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}$", args[2])
	if !meterIdFlag {
		return shim.Error("Incorrect MeterId of arguments. Expecting uuid. Not " + args[2])
	}
	eleFlag, _ := regexp.MatchString("^\\d+(\\.\\d{1,2})?$", args[3])
	if !eleFlag {
		return shim.Error("Incorrect Electricity of arguments. Expecting [^\\d+(\\.\\d{1,2})?$]. Not " + args[3])
	}
	accountFlag, _ := regexp.MatchString("^\\d+(\\.\\d{1,2})?$", args[4])
	if !accountFlag {
		return shim.Error("Incorrect Account of arguments. Expecting [^\\d+(\\.\\d{1,2})?$]. Not " + args[4])
	}
	tradingTimeFlag, _ := regexp.MatchString("^\\d{10}$", args[5])
	if !tradingTimeFlag {
		return shim.Error("Incorrect TradingTime of arguments. Expecting [^\\d{10}$]. Not " + args[5])
	}

	var transactionLedger = TransactionLedger{CompanyId: args[1], MeterId: args[2], Electricity: args[3], Account: args[4], TradingTime: args[5]}
	fmt.Println("New Added:", transactionLedger)

	transactionLedgerAsBytes, _ := json.Marshal(transactionLedger)
	fmt.Println("New args[0]:", "TL"+args[0])
	fmt.Println("New TransactionLedgerAsBytes:", transactionLedgerAsBytes)

	stub.PutState("TL"+args[0], transactionLedgerAsBytes)

	return shim.Success(nil)
}

func (s *SmartContract) query(stub shim.ChaincodeStubInterface, args []string) sc.Response {
	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting 1. Not " + strconv.Itoa(len(args)))
	}
	transactionLedgerAsBytes, _ := stub.GetState("TL"+args[0])
	return shim.Success(transactionLedgerAsBytes)
}

func (s *SmartContract) queryAll(stub shim.ChaincodeStubInterface, args []string) sc.Response {
	startKey := "TL0"
	endKey := "TL999999999"
	if len(args) == 1 {
		end, _ := regexp.MatchString("^\\d+$", args[0])
		if !end {
			return shim.Error("Incorrect EndKey of arguments. Expecting number. Not " + args[0])
		}
		endKey = "TL" + args[0]
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
		startKey = "TL" + args[0]
		endKey = "TL" + args[1]
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

	fmt.Printf("- listAllTransactionLedger:\n%s\n", buffer.String())

	return shim.Success(buffer.Bytes())
}

func main() {
	err := shim.Start(new(SmartContract))
	if err != nil {
		fmt.Printf("Error creating new Smart Contract: %s", err)
	}
}
