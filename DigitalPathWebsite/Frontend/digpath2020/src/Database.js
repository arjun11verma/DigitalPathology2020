const Realm = require("realm-web");

const appId = "digitalpathology2020-ecrjr";
const graphQLUri = `https://realm.mongodb.com/api/client/v2.0/app/${appId}/graphql`;

const mongo_app = new Realm.App({id: appId, timeout: 10000});

const login = async(email, password) => {
    await mongo_app.logIn(Realm.Credentials.emailPassword(email, password));
}

const createAccount = async(email, password) => {
    await mongo_app.emailPasswordAuth.registerUser(email, password);
}

const getAccessToken = async() => {
    await mongo_app.currentUser.refreshAccessToken();
    return mongo_app.currentUser.accessToken;
}

const checkLoggedIn = async() => {
    return (mongo_app.currentUser !== null && await mongo_app.currentUser.isLoggedIn);
}

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