
const hre = require("hardhat");
const fs = require("fs");
const path = require("path");

async function main() {
  console.log("🚀 Starting QuantumMailRegistry deployment...");

  // Get the deployer account
  const [deployer] = await hre.ethers.getSigners();
  console.log("📝 Deploying contract with account:", deployer.address);
  console.log("💰 Account balance:", (await hre.ethers.provider.getBalance(deployer.address)).toString());

  // Deploy the contract
  const QuantumMailRegistry = await hre.ethers.getContractFactory("QuantumMailRegistry");
  console.log("⏳ Deploying QuantumMailRegistry...");

  const contract = await QuantumMailRegistry.deploy();
  await contract.waitForDeployment();

  const contractAddress = await contract.getAddress();
  console.log("✅ QuantumMailRegistry deployed to:", contractAddress);

  // Save deployment info
  const deploymentInfo = {
    contractAddress: contractAddress,
    network: hre.network.name,
    deployer: deployer.address,
    deploymentTime: new Date().toISOString(),
    blockNumber: await hre.ethers.provider.getBlockNumber()
  };

  // Save to JSON file
  const deploymentPath = path.join(__dirname, "../deployment-info.json");
  fs.writeFileSync(deploymentPath, JSON.stringify(deploymentInfo, null, 2));
  console.log("📄 Deployment info saved to:", deploymentPath);

  // Save to Java resources for easy access
  const javaResourcePath = path.join(__dirname, "../src/main/resources/blockchain-config.json");
  const javaConfig = {
    contractAddress: contractAddress,
    network: hre.network.name,
    rpcUrl: hre.network.config.url || "http://127.0.0.1:7545",
    chainId: hre.network.config.chainId || 1337
  };
  fs.writeFileSync(javaResourcePath, JSON.stringify(javaConfig, null, 2));
  console.log("📄 Java config saved to:", javaResourcePath);

  // Verify contract is working by calling getContractStats
  console.log("\n🔍 Verifying contract deployment...");
  try {
    const stats = await contract.getContractStats();
    console.log("✅ Contract is functional");
    console.log("   Total emails registered:", stats[0].toString());
    console.log("   Verified emails:", stats[1].toString());
  } catch (error) {
    console.log("⚠️  Contract deployed but verification skipped");
  }

  console.log("\n✨ Deployment completed successfully!");
  console.log("════════════════════════════════════════");
  console.log("Contract Address:", contractAddress);
  console.log("Network:", hre.network.name);
  console.log("Block Number:", deploymentInfo.blockNumber);
  console.log("════════════════════════════════════════");
}

main()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error("❌ Deployment failed:", error);
    process.exit(1);
  });

