require("@nomicfoundation/hardhat-toolbox");
require("dotenv").config();

/** @type import('hardhat/config').HardhatUserConfig */
module.exports = {
  solidity: {
    version: "0.8.19",
    settings: {
      optimizer: {
        enabled: true,
        runs: 200
      }
    }
  },
  networks: {
    // Ganache local network (primary)
    ganache: {
      url: process.env.BLOCKCHAIN_RPC_URL || "http://127.0.0.1:7545",
      chainId: parseInt(process.env.BLOCKCHAIN_CHAIN_ID) || 1337,
      accounts: process.env.GANACHE_PRIVATE_KEY ? [process.env.GANACHE_PRIVATE_KEY] : []
    },
    // Hardhat local network
    localhost: {
      url: "http://127.0.0.1:8545",
      chainId: 31337
    },
    // Development network (alias for ganache)
    development: {
      url: process.env.BLOCKCHAIN_RPC_URL || "http://127.0.0.1:7545",
      chainId: parseInt(process.env.BLOCKCHAIN_CHAIN_ID) || 1337,
      accounts: process.env.GANACHE_PRIVATE_KEY ? [process.env.GANACHE_PRIVATE_KEY] : []
    }
  },
  paths: {
    sources: "./contracts",
    tests: "./test",
    cache: "./cache",
    artifacts: "./artifacts"
  }
};

