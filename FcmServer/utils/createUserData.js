const admin = require('../firebase');

async function createUserData(userRecord) {
  const firestore = admin.firestore();
  const userRef = firestore.collection('users').doc(userRecord.uid);

  const userData = {
    active: true,
    bio: "",
    blockedUsers: [],
    createdAt: admin.firestore.FieldValue.serverTimestamp(),
    email: userRecord.email || "",
    followerCount: 0,
    followers: [],
    following: [],
    followingCount: 0,
    friends: [],
    fullName: userRecord.displayName || "",
    id: userRecord.uid,
    language: "en",
    lastActive: admin.firestore.FieldValue.serverTimestamp(),
    privacySettings: {
      allowMessagesFrom: "everyone",
      postVisibility: "public",
      profileVisibility: "public"
    },
    profileImage: null,
    role: "USER",
    username: userRecord.displayName?.replace(/\s/g, "") || `user${Date.now()}`,
    videosCount: 0
  };

  const existingDoc = await userRef.get();
  if (!existingDoc.exists) {
    await userRef.set(userData);
    console.log(`Created Firestore user doc for ${userRecord.uid}`);
  } else {
    console.log(`User doc already exists for ${userRecord.uid}`);
  }
}

module.exports = createUserData;
