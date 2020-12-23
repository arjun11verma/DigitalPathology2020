/**
 * Singleton class for an instance of the MongoDB Realm App
 * @version 1.0
 * @author Arjun Verma
 */

const Realm = require("realm-web");

const appId = "digitalpathology2020-ecrjr";
const graphQLUri = `https://realm.mongodb.com/api/client/v2.0/app/${appId}/graphql`;

const mongo_app = new Realm.App({id: appId, timeout: 10000});

/**
 * Logs in a user with the provided email and password
 * @param {String} email 
 * @param {String} password 
 */
const login = async(email, password) => {
    await mongo_app.logIn(Realm.Credentials.emailPassword(email, password));
}

/**
 * Creates an Account with the provided email and password
 * @param {String} email 
 * @param {String} password 
 */
const createAccount = async(email, password) => {
    await mongo_app.emailPasswordAuth.registerUser(email, password);
}

/**
 * @returns Access token from the current user
 */
const getAccessToken = async() => {
    await mongo_app.currentUser.refreshAccessToken();
    return mongo_app.currentUser.accessToken;
}

/**
 * Checks whether the current user is logged in 
 * @returns {Boolean} Login status of the user
 */
const checkLoggedIn = async() => {
    return (mongo_app.currentUser !== null && await mongo_app.currentUser.isLoggedIn);
}

/**
 * Logs out the current user
 */
const logout = async() => {
    await mongo_app.currentUser.logOut();
}

export const app = mongo_app;
export const check = checkLoggedIn;
export const logIn = login;
export const accessToken = getAccessToken;
export const accessUri = graphQLUri;
export const create = createAccount;
export const logoutCurrentUser = logout;