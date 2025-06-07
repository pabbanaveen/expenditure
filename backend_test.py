#!/usr/bin/env python3
import requests
import json
import sys
import time
from typing import Dict, List, Any, Optional, Union
from pprint import pprint
from datetime import datetime

# Spring Boot backend URL (running on port 8080)
BASE_URL = "http://localhost:8080/api"

class ChittyManagerTester:
    def __init__(self, base_url: str):
        self.base_url = base_url
        self.session = requests.Session()
        self.chitty_id = None
        self.member_id = None
        self.test_results = {
            "total_tests": 0,
            "passed": 0,
            "failed": 0,
            "tests": []
        }

    def log_test(self, test_name: str, passed: bool, details: Dict[str, Any] = None):
        """Log test results"""
        result = "PASS" if passed else "FAIL"
        print(f"[{result}] {test_name}")
        if details and not passed:
            print(f"  Details: {json.dumps(details, indent=2)}")
        elif details and passed:
            print(f"  Info: {json.dumps(details, indent=2)}")
        
        self.test_results["total_tests"] += 1
        if passed:
            self.test_results["passed"] += 1
        else:
            self.test_results["failed"] += 1
        
        self.test_results["tests"].append({
            "name": test_name,
            "result": result,
            "details": details
        })

    def make_request(self, method: str, endpoint: str, data: Dict = None, expected_status: int = 200) -> Dict:
        """Make HTTP request and handle errors"""
        url = f"{self.base_url}{endpoint}"
        try:
            if method.lower() == "get":
                response = self.session.get(url)
            elif method.lower() == "post":
                response = self.session.post(url, json=data)
            elif method.lower() == "put":
                response = self.session.put(url, json=data)
            elif method.lower() == "delete":
                response = self.session.delete(url)
            else:
                raise ValueError(f"Unsupported HTTP method: {method}")
            
            if response.status_code != expected_status:
                print(f"Error: Expected status {expected_status}, got {response.status_code}")
                print(f"Response: {response.text}")
                return {"error": f"Status code {response.status_code}", "response": response.text}
            
            if response.text:
                try:
                    return response.json()
                except json.JSONDecodeError:
                    return {"text": response.text}
            return {"status": "success"}
        
        except requests.RequestException as e:
            print(f"Request error: {e}")
            return {"error": str(e)}

    def extract_data(self, response):
        """Extract data from standard Spring Boot response format"""
        if isinstance(response, dict) and "success" in response and "data" in response:
            if response["success"]:
                return response["data"]
            else:
                return {"error": response.get("message", "Unknown error")}
        return response

    def test_get_all_chitties(self) -> bool:
        """Test GET /api/chitties endpoint"""
        print("\n=== Testing GET /api/chitties ===")
        response = self.make_request("get", "/chitties")
        
        if "error" in response:
            self.log_test("Get All Chitties", False, {"error": response["error"]})
            return False
        
        # Extract data from response
        data = self.extract_data(response)
        
        if "error" in data:
            self.log_test("Get All Chitties", False, {"error": data["error"]})
            return False
        
        # Verify we have at least one chitty
        if not isinstance(data, list) or len(data) == 0:
            self.log_test("Get All Chitties", False, {"error": "No chitties found or invalid response format"})
            return False
        
        # Find the "5 Lakh Chitty" in the response
        five_lakh_chitty = None
        for chitty in data:
            if chitty.get("name") == "5 Lakh Chitty":
                five_lakh_chitty = chitty
                self.chitty_id = chitty.get("id")
                break
        
        if not five_lakh_chitty:
            self.log_test("Get All Chitties - Find 5 Lakh Chitty", False, {"error": "5 Lakh Chitty not found"})
            return False
        
        # Verify the mock data
        expected_data = {
            "name": "5 Lakh Chitty",
            "amount": 500000.0,
            "totalMonths": 20,
            "regularPayment": 25000.0,
            "liftedPayment": 31250.0,
            "totalMembers": 20
        }
        
        validation_errors = []
        for key, expected_value in expected_data.items():
            if key not in five_lakh_chitty:
                validation_errors.append(f"Missing field: {key}")
            elif five_lakh_chitty[key] != expected_value:
                validation_errors.append(f"Field {key}: expected {expected_value}, got {five_lakh_chitty[key]}")
        
        if validation_errors:
            self.log_test("Get All Chitties - Validate 5 Lakh Chitty", False, {"errors": validation_errors})
            return False
        
        # Get a member ID for later tests
        if "memberIds" in five_lakh_chitty and len(five_lakh_chitty["memberIds"]) > 0:
            self.member_id = five_lakh_chitty["memberIds"][0]
        
        self.log_test("Get All Chitties", True, {
            "chitty_count": len(data),
            "chitty_id": self.chitty_id,
            "member_id": self.member_id
        })
        return True

    def test_get_chitty_members(self) -> bool:
        """Test GET /api/chitties/{id}/members endpoint"""
        if not self.chitty_id:
            self.log_test("Get Chitty Members", False, {"error": "No chitty_id available. Run test_get_all_chitties first."})
            return False
        
        print(f"\n=== Testing GET /api/chitties/{self.chitty_id}/members ===")
        response = self.make_request("get", f"/chitties/{self.chitty_id}/members")
        
        if "error" in response:
            self.log_test("Get Chitty Members", False, {"error": response["error"]})
            return False
        
        # Extract data from response
        data = self.extract_data(response)
        
        if "error" in data:
            self.log_test("Get Chitty Members", False, {"error": data["error"]})
            return False
        
        # Verify we have 20 members
        if not isinstance(data, list):
            self.log_test("Get Chitty Members", False, {"error": "Invalid response format, expected list"})
            return False
        
        if len(data) != 20:
            self.log_test("Get Chitty Members", False, {"error": f"Expected 20 members, got {len(data)}"})
            return False
        
        # Check member structure
        if data and len(data) > 0:
            member = data[0]
            required_fields = ["id", "name", "lifted"]
            missing_fields = [field for field in required_fields if field not in member]
            
            if missing_fields:
                self.log_test("Get Chitty Members - Member Structure", False, 
                             {"error": f"Missing required fields: {', '.join(missing_fields)}"})
                return False
        
        self.log_test("Get Chitty Members", True, {"member_count": len(data)})
        return True

    def test_create_chitty(self) -> bool:
        """Test POST /api/chitties endpoint"""
        print("\n=== Testing POST /api/chitties ===")
        
        new_chitty = {
            "name": "Test Chitty",
            "amount": 100000.0,
            "totalMonths": 10,
            "startDate": datetime.now().strftime("%Y-%m-%dT%H:%M:%S")
        }
        
        response = self.make_request("post", "/chitties", new_chitty)
        
        if "error" in response:
            self.log_test("Create Chitty", False, {"error": response["error"]})
            return False
        
        # Extract data from response
        data = self.extract_data(response)
        
        if "error" in data:
            self.log_test("Create Chitty", False, {"error": data["error"]})
            return False
        
        # Verify the created chitty has an ID
        if "id" not in data:
            self.log_test("Create Chitty", False, {"error": "Created chitty doesn't have an ID"})
            return False
        
        # Verify the created chitty has the correct data
        validation_errors = []
        for key, value in new_chitty.items():
            if key not in data:
                validation_errors.append(f"Missing field: {key}")
            elif key == "startDate":
                # Skip exact date comparison
                continue
            elif data[key] != value:
                validation_errors.append(f"Field {key}: expected {value}, got {data[key]}")
        
        if validation_errors:
            self.log_test("Create Chitty - Validate Data", False, {"errors": validation_errors})
            return False
        
        # Verify payment calculations
        expected_regular_payment = new_chitty["amount"] / new_chitty["totalMonths"]
        expected_lifted_payment = expected_regular_payment * 1.25
        
        if "regularPayment" not in data or abs(data["regularPayment"] - expected_regular_payment) > 0.01:
            self.log_test("Create Chitty - Payment Calculation", False, 
                         {"error": f"Regular payment: expected {expected_regular_payment}, got {data.get('regularPayment', 'missing')}"})
            return False
        
        if "liftedPayment" not in data or abs(data["liftedPayment"] - expected_lifted_payment) > 0.01:
            self.log_test("Create Chitty - Payment Calculation", False, 
                         {"error": f"Lifted payment: expected {expected_lifted_payment}, got {data.get('liftedPayment', 'missing')}"})
            return False
        
        self.log_test("Create Chitty", True, {
            "created_chitty_id": data["id"],
            "regularPayment": data["regularPayment"],
            "liftedPayment": data["liftedPayment"]
        })
        return True

    def test_get_member_details(self) -> bool:
        """Test GET /api/members/{id} endpoint"""
        if not self.member_id:
            self.log_test("Get Member Details", False, {"error": "No member_id available. Run test_get_all_chitties first."})
            return False
        
        print(f"\n=== Testing GET /api/members/{self.member_id} ===")
        response = self.make_request("get", f"/members/{self.member_id}")
        
        if "error" in response:
            self.log_test("Get Member Details", False, {"error": response["error"]})
            return False
        
        # Extract data from response
        data = self.extract_data(response)
        
        if "error" in data:
            self.log_test("Get Member Details", False, {"error": data["error"]})
            return False
        
        # Verify member structure
        required_fields = ["id", "name", "lifted"]
        missing_fields = [field for field in required_fields if field not in data]
        
        if missing_fields:
            self.log_test("Get Member Details", False, 
                         {"error": f"Missing required fields: {', '.join(missing_fields)}"})
            return False
        
        self.log_test("Get Member Details", True, {
            "member_id": self.member_id,
            "name": data["name"],
            "lifted": data["lifted"]
        })
        return True

    def test_lift_member(self) -> bool:
        """Test POST /api/members/{id}/lift endpoint"""
        if not self.member_id:
            self.log_test("Lift Member", False, {"error": "No member_id available. Run test_get_all_chitties first."})
            return False
        
        print(f"\n=== Testing POST /api/members/{self.member_id}/lift ===")
        response = self.make_request("post", f"/members/{self.member_id}/lift")
        
        if "error" in response:
            self.log_test("Lift Member", False, {"error": response["error"]})
            return False
        
        # Extract data from response
        data = self.extract_data(response)
        
        if "error" in data:
            self.log_test("Lift Member", False, {"error": data["error"]})
            return False
        
        # Verify the member is now lifted
        if "lifted" not in data or data["lifted"] is not True:
            self.log_test("Lift Member", False, {"error": "Member not marked as lifted"})
            return False
        
        # Verify by getting the member details again
        verify_response = self.make_request("get", f"/members/{self.member_id}")
        verify_data = self.extract_data(verify_response)
        
        if "error" in verify_data:
            self.log_test("Lift Member - Verification", False, {"error": verify_data["error"]})
            return False
        
        if "lifted" not in verify_data or verify_data["lifted"] is not True:
            self.log_test("Lift Member - Verification", False, {"error": "Member not marked as lifted in verification"})
            return False
        
        self.log_test("Lift Member", True, {"member_id": self.member_id})
        return True

    def test_get_monthly_slips(self) -> bool:
        """Test GET /api/monthly-slips/chitty/{chittiId} endpoint"""
        if not self.chitty_id:
            self.log_test("Get Monthly Slips", False, {"error": "No chitty_id available. Run test_get_all_chitties first."})
            return False
        
        print(f"\n=== Testing GET /api/monthly-slips/chitty/{self.chitty_id} ===")
        response = self.make_request("get", f"/monthly-slips/chitty/{self.chitty_id}")
        
        if "error" in response:
            self.log_test("Get Monthly Slips", False, {"error": response["error"]})
            return False
        
        # Extract data from response
        data = self.extract_data(response)
        
        if "error" in data:
            self.log_test("Get Monthly Slips", False, {"error": data["error"]})
            return False
        
        # Verify we have monthly slips in the correct format
        if not isinstance(data, list):
            self.log_test("Get Monthly Slips", False, {"error": "Invalid response format, expected list"})
            return False
        
        # Check monthly slip structure if any exist
        if data and len(data) > 0:
            slip = data[0]
            required_fields = ["id", "chittyId", "month"]
            missing_fields = [field for field in required_fields if field not in slip]
            
            if missing_fields:
                self.log_test("Get Monthly Slips - Slip Structure", False, 
                             {"error": f"Missing required fields: {', '.join(missing_fields)}"})
                return False
        
        self.log_test("Get Monthly Slips", True, {"slip_count": len(data)})
        return True

    def test_generate_monthly_slip(self) -> bool:
        """Test POST /api/monthly-slips/generate endpoint"""
        if not self.chitty_id:
            self.log_test("Generate Monthly Slip", False, {"error": "No chitty_id available. Run test_get_all_chitties first."})
            return False
        
        print(f"\n=== Testing POST /api/monthly-slips/generate ===")
        
        slip_data = {
            "chittyId": self.chitty_id,
            "month": 1,
            "date": datetime.now().strftime("%Y-%m-%d")
        }
        
        response = self.make_request("post", "/monthly-slips/generate", slip_data)
        
        if "error" in response:
            self.log_test("Generate Monthly Slip", False, {"error": response["error"]})
            return False
        
        # Extract data from response
        data = self.extract_data(response)
        
        if "error" in data:
            self.log_test("Generate Monthly Slip", False, {"error": data["error"]})
            return False
        
        # Verify the generated slip has an ID
        if "id" not in data:
            self.log_test("Generate Monthly Slip", False, {"error": "Generated slip doesn't have an ID"})
            return False
        
        # Verify the slip has the correct data
        validation_errors = []
        for key, value in slip_data.items():
            if key not in data:
                validation_errors.append(f"Missing field: {key}")
            elif key == "date":
                # Skip exact date comparison
                continue
            elif data[key] != value:
                validation_errors.append(f"Field {key}: expected {value}, got {data[key]}")
        
        if validation_errors:
            self.log_test("Generate Monthly Slip - Validate Data", False, {"errors": validation_errors})
            return False
        
        self.log_test("Generate Monthly Slip", True, {"generated_slip_id": data["id"]})
        return True

    def run_all_tests(self):
        """Run all tests in sequence"""
        print("\n======= STARTING CHITTY MANAGER API TESTS =======\n")
        
        # Test server connectivity
        try:
            response = requests.get(f"{self.base_url}/chitties", timeout=5)
            print(f"Server connection: {'OK' if response.status_code == 200 else 'FAILED'}")
        except requests.RequestException as e:
            print(f"Server connection failed: {e}")
            print(f"Make sure the Spring Boot backend is running on {self.base_url}")
            return
        
        # Run all tests
        self.test_get_all_chitties()
        self.test_get_chitty_members()
        self.test_create_chitty()
        self.test_get_member_details()
        self.test_lift_member()
        self.test_get_monthly_slips()
        self.test_generate_monthly_slip()
        
        # Print summary
        print("\n======= TEST SUMMARY =======")
        print(f"Total tests: {self.test_results['total_tests']}")
        print(f"Passed: {self.test_results['passed']}")
        print(f"Failed: {self.test_results['failed']}")
        print(f"Success rate: {(self.test_results['passed'] / self.test_results['total_tests']) * 100:.2f}%")
        
        if self.test_results['failed'] > 0:
            print("\nFailed tests:")
            for test in self.test_results['tests']:
                if test['result'] == 'FAIL':
                    print(f"- {test['name']}")

if __name__ == "__main__":
    tester = ChittyManagerTester(BASE_URL)
    tester.run_all_tests()
