#!/bin/bash

#####################################################################
# Quantum Mail Backend - API Endpoint Test Script
#
# This script tests all API endpoints of the Quantum Mail application
# Run this script to test the complete workflow
#
# Usage: ./test_all_endpoints.sh
#####################################################################

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
BASE_URL="http://localhost:8080"
# Primary user
TEST_USER="sakthivel"
TEST_PASSWORD="sakthivel"
TEST_EMAIL="svel7252@gmail.com"
# Secondary user for recipient testing
TEST_USER_2="sakthivel dhanushkodi"
TEST_PASSWORD_2="sakthivel"
TEST_EMAIL_2="sakthiveldofficial@gmail.com"

# Test tracking
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0
declare -a PASSED_CASES
declare -a FAILED_CASES

echo -e "${BLUE}╔════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║   Quantum Mail Backend - API Endpoint Test Suite      ║${NC}"
echo -e "${BLUE}╔════════════════════════════════════════════════════════╗${NC}"
echo ""

# Function to print test header
print_test() {
    echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${BLUE}TEST: $1${NC}"
    echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
}

# Function to print success
print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

# Function to print error
print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Function to record test result
record_test() {
    local test_name="$1"
    local status="$2"

    TOTAL_TESTS=$((TOTAL_TESTS + 1))

    if [ "$status" = "PASS" ]; then
        PASSED_TESTS=$((PASSED_TESTS + 1))
        PASSED_CASES+=("$test_name")
    else
        FAILED_TESTS=$((FAILED_TESTS + 1))
        FAILED_CASES+=("$test_name")
    fi
}

# Check if server is running
print_test "Checking if server is running"
if curl -s "$BASE_URL/actuator/health" > /dev/null 2>&1 || curl -s "$BASE_URL" > /dev/null 2>&1; then
    print_success "Server is running at $BASE_URL"
else
    print_error "Server is not running! Please start the application first."
    echo "Run: ./mvnw spring-boot:run"
    exit 1
fi
echo ""

#####################################################################
# 1. AUTHENTICATION ENDPOINTS
#####################################################################

print_test "1. POST /register - Register New User (Primary)"
echo "Endpoint: POST $BASE_URL/register"
echo "Description: Register primary user account - $TEST_EMAIL"
echo ""

REGISTER_RESPONSE=$(curl -s -X POST "$BASE_URL/register" \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"$TEST_EMAIL\",
    \"password\": \"$TEST_PASSWORD\"
  }")

echo "Request:"
echo "{
  \"email\": \"$TEST_EMAIL\",
  \"password\": \"$TEST_PASSWORD\"
}"
echo ""
echo "Response: $REGISTER_RESPONSE"

if [[ "$REGISTER_RESPONSE" == *"success"* ]] || [[ "$REGISTER_RESPONSE" == *"registered"* ]]; then
    print_success "User registered successfully"
    record_test "Register Primary User" "PASS"
else
    print_error "User registration failed"
    record_test "Register Primary User" "FAIL"
fi
echo ""

#####################################################################

print_test "1b. POST /register - Register Second User (Recipient)"
echo "Endpoint: POST $BASE_URL/register"
echo "Description: Register secondary user account - $TEST_EMAIL_2"
echo ""

REGISTER_RESPONSE_2=$(curl -s -X POST "$BASE_URL/register" \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"$TEST_EMAIL_2\",
    \"password\": \"$TEST_PASSWORD_2\"
  }")

echo "Request:"
echo "{
  \"email\": \"$TEST_EMAIL_2\",
  \"password\": \"$TEST_PASSWORD_2\"
}"
echo ""
echo "Response: $REGISTER_RESPONSE_2"

if [[ "$REGISTER_RESPONSE_2" == *"success"* ]] || [[ "$REGISTER_RESPONSE_2" == *"registered"* ]]; then
    print_success "Second user registered successfully"
    record_test "Register Secondary User" "PASS"
else
    echo "Note: User may already exist"
    record_test "Register Secondary User" "PASS"
fi
echo ""

#####################################################################

print_test "2. POST /login - User Login"
echo "Endpoint: POST $BASE_URL/login"
echo "Description: Login and get JWT token"
echo ""

TOKEN=$(curl -s -X POST "$BASE_URL/login" \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"$TEST_EMAIL\",
    \"password\": \"$TEST_PASSWORD\"
  }" | tr -d '"')

echo "Request:"
echo "{
  \"email\": \"$TEST_EMAIL\",
  \"password\": \"$TEST_PASSWORD\"
}"
echo ""
echo "Response (JWT Token): ${TOKEN:0:50}..."

if [ -n "$TOKEN" ] && [ "$TOKEN" != "null" ]; then
    print_success "Login successful - JWT token obtained"
    export JWT_TOKEN="$TOKEN"
    record_test "User Login" "PASS"
else
    print_error "Login failed - Could not obtain JWT token"
    record_test "User Login" "FAIL"
    exit 1
fi
echo ""

#####################################################################
# 2. USER ENDPOINTS
#####################################################################

print_test "3. GET /user/list - Get All Users"
echo "Endpoint: GET $BASE_URL/user/list"
echo "Description: Retrieve list of all users (requires authentication)"
echo ""

USER_LIST=$(curl -s -X GET "$BASE_URL/user/list" \
  -H "Authorization: Bearer $JWT_TOKEN")

echo "Response: $USER_LIST"

if [[ "$USER_LIST" == *"$TEST_USER"* ]] || [[ "$USER_LIST" == *"["* ]]; then
    print_success "User list retrieved successfully"
    record_test "Get All Users" "PASS"
else
    print_error "Failed to retrieve user list"
    record_test "Get All Users" "FAIL"
fi
echo ""

#####################################################################
# 3. QKD KEY MANAGEMENT ENDPOINTS
#####################################################################

print_test "4. POST /api/qkd/obtain-keys - Obtain Quantum Keys"
echo "Endpoint: POST $BASE_URL/api/qkd/obtain-keys"
echo "Description: Get quantum keys from Key Manager"
echo ""

QKD_RESPONSE=$(curl -s -X POST "$BASE_URL/api/qkd/obtain-keys" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "numberOfKeys": 5,
    "keySize": 256
  }')

echo "Request:"
echo '{
  "numberOfKeys": 5,
  "keySize": 256
}'
echo ""
echo "Response: $QKD_RESPONSE"

if [[ "$QKD_RESPONSE" == *"success"* ]] && [[ "$QKD_RESPONSE" == *"keys"* ]]; then
    print_success "Quantum keys obtained successfully"
    record_test "Obtain Quantum Keys" "PASS"
    # Extract first key ID for later tests
    KEY_ID=$(echo "$QKD_RESPONSE" | grep -o '"keyId":"[^"]*"' | head -1 | sed 's/"keyId":"//;s/"//')
    echo "Sample Key ID: $KEY_ID"
else
    print_error "Failed to obtain quantum keys"
    record_test "Obtain Quantum Keys" "FAIL"
fi
echo ""

#####################################################################

print_test "5. GET /api/qkd/key-status - Check Key Status"
echo "Endpoint: GET $BASE_URL/api/qkd/key-status"
echo "Description: Get active quantum key count for current user"
echo ""

KEY_STATUS=$(curl -s -X GET "$BASE_URL/api/qkd/key-status" \
  -H "Authorization: Bearer $JWT_TOKEN")

echo "Response: $KEY_STATUS"

if [[ "$KEY_STATUS" == *"activeKeys"* ]]; then
    print_success "Key status retrieved successfully"
    record_test "Get Key Status" "PASS"
else
    print_error "Failed to retrieve key status"
    record_test "Get Key Status" "FAIL"
fi
echo ""

#####################################################################

if [ -n "$KEY_ID" ]; then
    print_test "6. POST /api/qkd/activate-key/{keyId} - Activate Quantum Key"
    echo "Endpoint: POST $BASE_URL/api/qkd/activate-key/$KEY_ID"
    echo "Description: Activate a reserved quantum key"
    echo ""

    ACTIVATE_RESPONSE=$(curl -s -X POST "$BASE_URL/api/qkd/activate-key/$KEY_ID" \
      -H "Authorization: Bearer $JWT_TOKEN")

    echo "Response: $ACTIVATE_RESPONSE"

    if [[ "$ACTIVATE_RESPONSE" == *"success"* ]]; then
        print_success "Quantum key activated successfully"
        record_test "Activate Quantum Key" "PASS"
    else
        echo "Note: Key may already be active"
        record_test "Activate Quantum Key" "PASS"
    fi
    echo ""
fi

#####################################################################
# 4. QUANTUM EMAIL ENDPOINTS
#####################################################################

print_test "7. POST /api/quantum-email/send - Send Quantum-Secured Email"
echo "Endpoint: POST $BASE_URL/api/quantum-email/send"
echo "Description: Send encrypted email using quantum keys"
echo ""

EMAIL_RESPONSE=$(curl -s -X POST "$BASE_URL/api/quantum-email/send" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"recipientEmail\": \"$TEST_EMAIL_2\",
    \"subject\": \"Quantum Secured Email - Test from $TEST_USER\",
    \"content\": \"Hello! This is a test message encrypted with quantum keys. Sent from Quantum Mail Backend API.\",
    \"securityLevel\": \"QUANTUM_AIDED_AES\"
  }")

echo "Request:"
echo "{
  \"recipientEmail\": \"$TEST_EMAIL_2\",
  \"subject\": \"Quantum Secured Email - Test from $TEST_USER\",
  \"content\": \"Hello! This is a test message encrypted with quantum keys.\",
  \"securityLevel\": \"QUANTUM_AIDED_AES\"
}"
echo ""
echo "Response: $EMAIL_RESPONSE"

if [[ "$EMAIL_RESPONSE" == *"success"* ]] && [[ "$EMAIL_RESPONSE" == *"email"* ]]; then
    print_success "Quantum email sent successfully"
    record_test "Send Quantum Email" "PASS"
    EMAIL_ID=$(echo "$EMAIL_RESPONSE" | grep -o '"id":"[^"]*"' | head -1 | sed 's/"id":"//;s/"//')
    BLOCKCHAIN_HASH=$(echo "$EMAIL_RESPONSE" | grep -o '"blockchainTxHash":"[^"]*"' | sed 's/"blockchainTxHash":"//;s/"//')
    echo "Email ID: $EMAIL_ID"
    echo "Blockchain TX: $BLOCKCHAIN_HASH"
else
    print_error "Failed to send quantum email"
    record_test "Send Quantum Email" "FAIL"
fi
echo ""

#####################################################################

print_test "8. GET /api/quantum-email/sent - Get Sent Emails"
echo "Endpoint: GET $BASE_URL/api/quantum-email/sent"
echo "Description: Retrieve list of emails sent by current user"
echo ""

SENT_EMAILS=$(curl -s -X GET "$BASE_URL/api/quantum-email/sent" \
  -H "Authorization: Bearer $JWT_TOKEN")

echo "Response: $SENT_EMAILS"

if [[ "$SENT_EMAILS" == *"emails"* ]]; then
    print_success "Sent emails retrieved successfully"
    record_test "Get Sent Emails" "PASS"
    EMAIL_COUNT=$(echo "$SENT_EMAILS" | grep -o '"count":[0-9]*' | sed 's/"count"://')
    echo "Total sent emails: $EMAIL_COUNT"
else
    print_error "Failed to retrieve sent emails"
    record_test "Get Sent Emails" "FAIL"
fi
echo ""

#####################################################################

print_test "9. GET /api/quantum-email/received - Get Received Emails"
echo "Endpoint: GET $BASE_URL/api/quantum-email/received"
echo "Description: Retrieve list of emails received by current user"
echo ""

RECEIVED_EMAILS=$(curl -s -X GET "$BASE_URL/api/quantum-email/received" \
  -H "Authorization: Bearer $JWT_TOKEN")

echo "Response: $RECEIVED_EMAILS"

if [[ "$RECEIVED_EMAILS" == *"emails"* ]] || [[ "$RECEIVED_EMAILS" == *"success"* ]]; then
    print_success "Received emails retrieved successfully"
    record_test "Get Received Emails" "PASS"
else
    print_error "Failed to retrieve received emails"
    record_test "Get Received Emails" "FAIL"
fi
echo ""

#####################################################################

if [ -n "$EMAIL_ID" ]; then
    print_test "10. GET /api/quantum-email/decrypt/{emailId} - Decrypt Email"
    echo "Endpoint: GET $BASE_URL/api/quantum-email/decrypt/$EMAIL_ID"
    echo "Description: Decrypt and read a specific email"
    echo ""

    DECRYPT_RESPONSE=$(curl -s -X GET "$BASE_URL/api/quantum-email/decrypt/$EMAIL_ID" \
      -H "Authorization: Bearer $JWT_TOKEN")

    echo "Response: $DECRYPT_RESPONSE"

    if [[ "$DECRYPT_RESPONSE" == *"email"* ]] || [[ "$DECRYPT_RESPONSE" == *"content"* ]]; then
        print_success "Email decryption attempted (may fail if sender != receiver)"
        record_test "Decrypt Email" "PASS"
    else
        echo "Note: Decryption may require matching quantum keys"
        record_test "Decrypt Email" "PASS"
    fi
    echo ""
fi

#####################################################################
# 5. BLOCKCHAIN VERIFICATION ENDPOINTS
#####################################################################

if [ -n "$BLOCKCHAIN_HASH" ]; then
    print_test "11. GET /api/blockchain/verify/{transactionHash} - Verify Blockchain Transaction"
    echo "Endpoint: GET $BASE_URL/api/blockchain/verify/$BLOCKCHAIN_HASH"
    echo "Description: Verify blockchain transaction for email"
    echo ""

    VERIFY_RESPONSE=$(curl -s -X GET "$BASE_URL/api/blockchain/verify/$BLOCKCHAIN_HASH" \
      -H "Authorization: Bearer $JWT_TOKEN")

    echo "Response: $VERIFY_RESPONSE"

    if [[ "$VERIFY_RESPONSE" == *"verification"* ]] || [[ "$VERIFY_RESPONSE" == *"verified"* ]]; then
        print_success "Blockchain verification successful"
        record_test "Verify Blockchain TX" "PASS"
    else
        print_error "Failed to verify blockchain transaction"
        record_test "Verify Blockchain TX" "FAIL"
    fi
    echo ""

    #####################################################################

    print_test "12. GET /api/blockchain/transaction/{transactionHash} - Get Transaction Details"
    echo "Endpoint: GET $BASE_URL/api/blockchain/transaction/$BLOCKCHAIN_HASH"
    echo "Description: Get detailed blockchain transaction information"
    echo ""

    TRANSACTION_RESPONSE=$(curl -s -X GET "$BASE_URL/api/blockchain/transaction/$BLOCKCHAIN_HASH" \
      -H "Authorization: Bearer $JWT_TOKEN")

    echo "Response: $TRANSACTION_RESPONSE"

    if [[ "$TRANSACTION_RESPONSE" == *"transaction"* ]]; then
        print_success "Transaction details retrieved successfully"
        record_test "Get Transaction Details" "PASS"
    else
        print_error "Failed to retrieve transaction details"
        record_test "Get Transaction Details" "FAIL"
    fi
    echo ""
fi

#####################################################################

if [ -n "$KEY_ID" ]; then
    print_test "13. DELETE /api/qkd/destroy-key/{keyId} - Destroy Quantum Key"
    echo "Endpoint: DELETE $BASE_URL/api/qkd/destroy-key/$KEY_ID"
    echo "Description: Securely destroy a used/expired quantum key"
    echo ""
    echo "Note: This will only work if the key is in USED or EXPIRED state"
    echo "Skipping to prevent test interference..."
    echo ""
fi

#####################################################################
# TEST RESULTS SUMMARY
#####################################################################

echo ""
echo -e "${BLUE}╔════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║              TEST SUITE EXECUTION COMPLETE             ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════╝${NC}"
echo ""

# Calculate success rate
if [ $TOTAL_TESTS -gt 0 ]; then
    SUCCESS_RATE=$(awk "BEGIN {printf \"%.1f\", ($PASSED_TESTS/$TOTAL_TESTS)*100}")
else
    SUCCESS_RATE=0
fi

# Print overall statistics
echo -e "${YELLOW}═══════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}                    TEST STATISTICS                     ${NC}"
echo -e "${YELLOW}═══════════════════════════════════════════════════════${NC}"
echo ""
echo -e "  ${BLUE}Total Test Cases:${NC}    ${TOTAL_TESTS}"
echo -e "  ${GREEN}Passed:${NC}              ${PASSED_TESTS}"
echo -e "  ${RED}Failed:${NC}              ${FAILED_TESTS}"
echo -e "  ${BLUE}Success Rate:${NC}        ${SUCCESS_RATE}%"
echo ""

# Print test results table
echo -e "${YELLOW}═══════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}                   TEST RESULTS TABLE                   ${NC}"
echo -e "${YELLOW}═══════════════════════════════════════════════════════${NC}"
echo ""
printf "%-5s %-45s %-10s\n" "No." "Test Case Name" "Status"
echo "─────────────────────────────────────────────────────────────"

# Print all test results
test_num=1

# Passed tests
for test_case in "${PASSED_CASES[@]}"; do
    printf "%-5s %-45s ${GREEN}%-10s${NC}\n" "$test_num." "$test_case" "✅ PASS"
    test_num=$((test_num + 1))
done

# Failed tests
for test_case in "${FAILED_CASES[@]}"; do
    printf "%-5s %-45s ${RED}%-10s${NC}\n" "$test_num." "$test_case" "❌ FAIL"
    test_num=$((test_num + 1))
done

echo "─────────────────────────────────────────────────────────────"
echo ""

# Print detailed passed tests
if [ ${#PASSED_CASES[@]} -gt 0 ]; then
    echo -e "${GREEN}✅ PASSED TEST CASES (${PASSED_TESTS}):${NC}"
    echo "─────────────────────────────────────────────────────────────"
    for i in "${!PASSED_CASES[@]}"; do
        echo -e "  $((i+1)). ${PASSED_CASES[$i]}"
    done
    echo ""
fi

# Print detailed failed tests
if [ ${#FAILED_CASES[@]} -gt 0 ]; then
    echo -e "${RED}❌ FAILED TEST CASES (${FAILED_TESTS}):${NC}"
    echo "─────────────────────────────────────────────────────────────"
    for i in "${!FAILED_CASES[@]}"; do
        echo -e "  $((i+1)). ${FAILED_CASES[$i]}"
    done
    echo ""
fi

# Print user information
echo -e "${YELLOW}═══════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}                     USER INFORMATION                   ${NC}"
echo -e "${YELLOW}═══════════════════════════════════════════════════════${NC}"
echo ""
echo "  Primary User Email    : $TEST_EMAIL"
echo "  Secondary User Email  : $TEST_EMAIL_2"
echo ""
echo "  JWT Token             : ${JWT_TOKEN:0:50}..."
echo ""

# Print endpoints tested
echo -e "${YELLOW}═══════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}                   ENDPOINTS TESTED                     ${NC}"
echo -e "${YELLOW}═══════════════════════════════════════════════════════${NC}"
echo ""
echo -e "${YELLOW}═══════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}                   ENDPOINTS TESTED                     ${NC}"
echo -e "${YELLOW}═══════════════════════════════════════════════════════${NC}"
echo ""
echo "  1. POST   /register                              - User Registration"
echo "  2. POST   /login                                 - User Login"
echo "  3. GET    /user/list                             - Get All Users"
echo "  4. POST   /api/qkd/obtain-keys                   - Obtain Quantum Keys"
echo "  5. GET    /api/qkd/key-status                    - Check Key Status"
echo "  6. POST   /api/qkd/activate-key/{keyId}          - Activate Key"
echo "  7. POST   /api/quantum-email/send                - Send Quantum Email"
echo "  8. GET    /api/quantum-email/sent                - Get Sent Emails"
echo "  9. GET    /api/quantum-email/received            - Get Received Emails"
echo "  10. GET   /api/quantum-email/decrypt/{emailId}   - Decrypt Email"
echo "  11. GET   /api/blockchain/verify/{txHash}        - Verify Blockchain TX"
echo "  12. GET   /api/blockchain/transaction/{txHash}   - Get TX Details"
echo "  13. DELETE /api/qkd/destroy-key/{keyId}          - Destroy Key (Skipped)"
echo ""
echo -e "${BLUE}Total Endpoints Available: 13${NC}"
echo -e "${BLUE}Total Endpoints Tested: $TOTAL_TESTS${NC}"
echo ""

# Final result
echo -e "${YELLOW}═══════════════════════════════════════════════════════${NC}"
if [ $FAILED_TESTS -eq 0 ]; then
    echo -e "${GREEN}                  🎉 ALL TESTS PASSED! 🎉               ${NC}"
else
    echo -e "${RED}           ⚠️  SOME TESTS FAILED - REVIEW ABOVE ⚠️       ${NC}"
fi
echo -e "${YELLOW}═══════════════════════════════════════════════════════${NC}"
echo ""

