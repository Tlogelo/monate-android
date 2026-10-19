package com.example.monatechickensteak.data

import com.example.monatechickensteak.model.Faq
import com.example.monatechickensteak.model.FoodItem
import com.example.monatechickensteak.model.OrderStatus
import com.example.monatechickensteak.model.Promotion
import com.example.monatechickensteak.model.Reward

/**
 * Starting data for the app database. It is written into SQLite the first time the
 * app runs (see MonateDbHelper). Every table gets at least 10 records, and a unit test
 * (SeedDataTest) checks that, so the requirement is checked on every push.
 *
 * All sample customers share the password  Monate@123  (stored only as a salted hash).
 */
object SeedData {

    const val DELIVERY_FEE = 25.0
    const val DEMO_PASSWORD = "Monate@123"

    data class SeedUser(
        val id: Int,
        val name: String,
        val email: String,
        val passwordHash: String,
        val points: Int,
        val createdAt: String
    )

    data class SeedCategory(val id: Int, val name: String)

    data class SeedMenuItem(
        val id: Int,
        val categoryId: Int,
        val name: String,
        val description: String,
        val price: Double,
        val emoji: String
    )

    data class SeedOrder(
        val id: Int,
        val userId: Int,
        /** Pairs of (menu item id, quantity). */
        val lines: List<Pair<Int, Int>>,
        val collectionTime: String,
        val paymentMethod: String,
        val status: OrderStatus,
        val placedAt: String
    )

    data class SeedReview(
        val id: Int,
        val userId: Int,
        val itemName: String,
        val rating: Int,
        val comment: String,
        val author: String,
        val createdAt: String
    )

    data class SeedRedemption(
        val id: Int,
        val userId: Int,
        val rewardId: Int,
        val code: String,
        val redeemedAt: String
    )

    data class SeedSupportMessage(
        val id: Int,
        val userId: Int,
        val message: String,
        val createdAt: String
    )

    private fun hashed(salt: String, hash: String) =
        listOf("pbkdf2_sha256", "120000", salt, hash).joinToString("\$")

    val users = listOf(
        SeedUser(1, "Demo Customer", "demo@monate.co.za", hashed("e786734bff28a0306446459a9f82011a", "989223c8e3eade077f2be355f36c6da7a0674a1580a55a5649dfee868cfa2117"), 120, "01 Oct 2026"),
        SeedUser(2, "Thabo Nkosi", "thabo.nkosi@example.com", hashed("e412653bb5b14af0ebabf0448ba64a38", "23404887ce1956b7030d13d0b0761f1d7e75e7b1728f5f4ed009ff119295c624"), 340, "01 Oct 2026"),
        SeedUser(3, "Lerato Khumalo", "lerato.khumalo@example.com", hashed("13f6c7dda1b7f581a0ccd6d48288bec6", "f1d7112856b10fa1e3effc3a55c37d043507855952cf8cca6f6dbeb3b9151722"), 85, "02 Oct 2026"),
        SeedUser(4, "Sipho Mahlangu", "sipho.mahlangu@example.com", hashed("006d83e44313e57662597dab172ed27e", "452bfe44f0e7281bc940101ce8a20aa0f10e28bc0497b6dd103dbb48d400c9f3"), 210, "02 Oct 2026"),
        SeedUser(5, "Naledi Dlamini", "naledi.dlamini@example.com", hashed("1c5f24dd3facb38437bb249ca1a6222f", "7fd18ec87f7f5a929f70ecbcade9f6f4a32f7d58802b747078ca4b01d5b7f618"), 45, "03 Oct 2026"),
        SeedUser(6, "Kagiso Molefe", "kagiso.molefe@example.com", hashed("8ad19297343d3ed93c3bcadba9a69c2a", "013a8f43c640b91c1220ba53fcfb78f20f72cbc60a3f503ed3f7b0c0b7f30b6b"), 150, "03 Oct 2026"),
        SeedUser(7, "Zanele Mthembu", "zanele.mthembu@example.com", hashed("df0b923d308e566ec943dede91d490ed", "f86e5c81cfee19a93e16c5797bc5db4337637b65b79f521f1f95abcec446f906"), 60, "04 Oct 2026"),
        SeedUser(8, "Palesa Sithole", "palesa.sithole@example.com", hashed("f554b3fe1c8caf7235f257f65136ee66", "05539bf25a7684492dbcf6d8c71bbe31dd3af4c8018d6b27d14fcf50c47d5258"), 305, "05 Oct 2026"),
        SeedUser(9, "Bongani Zulu", "bongani.zulu@example.com", hashed("4d97d04f31ea1339b8bf16b68a84779c", "7e9dd5d47096162249ec49a7954e03599925f5600f49d86397ffaa6644c94c76"), 95, "06 Oct 2026"),
        SeedUser(10, "Refilwe Maseko", "refilwe.maseko@example.com", hashed("81f6629cf595df2cd59c484e69179f9b", "d654ae34c276aea3532907b0386278fa73ce7be36a6a03324b6be23f6d8e556b"), 130, "07 Oct 2026")
    )

    val categories = listOf(
        SeedCategory(1, "Burgers"),
        SeedCategory(2, "Chicken"),
        SeedCategory(3, "Desserts"),
        SeedCategory(4, "Drinks"),
        SeedCategory(5, "Pizza"),
        SeedCategory(6, "Sides"),
        SeedCategory(7, "Steak"),
        SeedCategory(8, "Wings"),
        SeedCategory(9, "Wraps"),
        SeedCategory(10, "Combos")
    )

    val menuItems = listOf(
        SeedMenuItem(1, 2, "Quarter Chicken", "Flame-grilled chicken with your choice of side.", 75.0, "🍗"),
        SeedMenuItem(2, 7, "Rump 300g", "Juicy rump steak served with a side.", 180.0, "🥩"),
        SeedMenuItem(3, 8, "6 Wings", "Peri-peri wings served with a side.", 95.0, "🍖"),
        SeedMenuItem(4, 1, "Cheese Burger", "Beef patty, cheese, lettuce, tomato & sauce.", 55.0, "🍔"),
        SeedMenuItem(5, 3, "Malva Pudding", "Traditional Malva pudding & custard.", 28.0, "🍮"),
        SeedMenuItem(6, 4, "Coca Cola 330ml", "Refreshing Coca Cola soft drink.", 20.0, "🥤"),
        SeedMenuItem(7, 4, "Sprite 330ml", "Refreshing Sprite soft drink.", 20.0, "🥤"),
        SeedMenuItem(8, 4, "Still Water 500ml", "Bottled still water.", 15.0, "💧"),
        SeedMenuItem(9, 6, "Pap & Gravy", "Traditional pap with tasty gravy.", 20.0, "🍲"),
        SeedMenuItem(10, 6, "Chips", "Crispy golden fried chips.", 25.0, "🍟"),
        SeedMenuItem(11, 5, "Margherita Pizza", "Cheese & tomato classic pizza.", 85.0, "🍕"),
        SeedMenuItem(12, 9, "Chicken Wrap", "Grilled chicken with lettuce & sauce.", 65.0, "🌯"),
        SeedMenuItem(13, 2, "Half Chicken", "Half a flame-grilled chicken with your choice of side.", 130.0, "🍗"),
        SeedMenuItem(14, 7, "Fillet 250g", "Tender fillet steak served with a side.", 165.0, "🥩"),
        SeedMenuItem(15, 7, "T-Bone 400g", "Flame-grilled T-bone served with a side.", 220.0, "🥩"),
        SeedMenuItem(16, 8, "12 Wings", "A dozen peri-peri wings served with a side.", 175.0, "🍖"),
        SeedMenuItem(17, 1, "Double Cheese Burger", "Two beef patties, double cheese & sauce.", 79.0, "🍔"),
        SeedMenuItem(18, 1, "Crumbed Burger", "Crumbed breast fillet, lettuce, mayo & tomato.", 60.0, "🍔"),
        SeedMenuItem(19, 3, "Ice Cream Cup", "Vanilla ice cream with chocolate sauce.", 25.0, "🍦"),
        SeedMenuItem(20, 4, "Fanta Orange 330ml", "Refreshing Fanta Orange soft drink.", 20.0, "🥤"),
        SeedMenuItem(21, 5, "Pepperoni Pizza", "Pepperoni, cheese & tomato pizza.", 95.0, "🍕"),
        SeedMenuItem(22, 6, "Coleslaw", "Creamy homemade coleslaw.", 18.0, "🥗"),
        SeedMenuItem(23, 9, "Steak Wrap", "Strips of grilled steak with lettuce & sauce.", 75.0, "🌯"),
        SeedMenuItem(24, 10, "Family Combo", "Whole chicken, large chips and a 2L cold drink.", 320.0, "🍱"),
        SeedMenuItem(25, 10, "Solo Combo", "Quarter chicken, chips and a 330ml drink.", 99.0, "🍱")
    )

    val rewards = listOf(
        Reward(1, "Free drink", 50, "🥤"),
        Reward(2, "Free chips", 80, "🍟"),
        Reward(3, "R50 off your order", 200, "💸"),
        Reward(4, "Free coleslaw", 40, "🥗"),
        Reward(5, "Free Malva Pudding", 90, "🍮"),
        Reward(6, "Free Cheese Burger", 150, "🍔"),
        Reward(7, "Free 6 Wings", 220, "🍖"),
        Reward(8, "Free Margherita Pizza", 250, "🍕"),
        Reward(9, "R100 off your order", 380, "💰"),
        Reward(10, "Free Family Combo", 600, "🍱")
    )

    val promotions = listOf(
        Promotion(1, "Family Combo", "Whole chicken, large chips and a 2L cold drink for R320.", "🍱"),
        Promotion(2, "Solo Combo", "Quarter chicken, chips and a 330ml drink for R99.", "🍗"),
        Promotion(3, "Earn as you eat", "Get 1 loyalty point for every R10 you spend.", "⭐"),
        Promotion(4, "Review & earn", "Review a meal and get 5 bonus loyalty points.", "💬"),
        Promotion(5, "Rewards waiting", "Redeem a free drink from just 50 points.", "🎁"),
        Promotion(6, "Flame-grilled favourite", "Quarter Chicken with your choice of side for R75.", "🔥"),
        Promotion(7, "Steak lovers", "Rump 300g with a side for R180.", "🥩"),
        Promotion(8, "Wing time", "Six peri-peri wings with a side for R95.", "🍖"),
        Promotion(9, "Sweet finish", "Traditional Malva pudding & custard for R28.", "🍮"),
        Promotion(10, "Order ahead", "Pick a collection time at checkout and skip the queue.", "⏱️")
    )

    val faqs = listOf(
        Faq(1, "How do I track my order?",
            "Open the Orders tab. The progress bar moves from Received to Preparing, Ready and Delivered as the kitchen updates your order."),
        Faq(2, "Can I change or cancel my order?",
            "Call us straight away. Once an order is Preparing it can no longer be changed."),
        Faq(3, "How do I earn loyalty points?",
            "You earn 1 point for every R10 you spend and 5 points for every review. Redeem them under Profile > Loyalty & rewards."),
        Faq(4, "Which payment methods do you accept?",
            "Cash on collection, card payment and other payment options. You choose at checkout."),
        Faq(5, "How do I change my password?",
            "Go to Profile and use the Change password section. If you cannot log in, contact us and we will help."),
        Faq(6, "What are your opening hours?",
            "Monday to Thursday 11:00 AM - 10:00 PM\nFriday and Saturday 11:00 AM - 11:00 PM\nSunday 11:00 AM - 9:00 PM"),
        Faq(7, "Is there a delivery fee?",
            "A flat fee of R25 is added to every order. You can see it in the order summary before you place the order."),
        Faq(8, "Can I log in with Google?",
            "Yes. Tap Continue with Google on the login screen and choose your Google account. No separate password is needed."),
        Faq(9, "Is my password safe?",
            "Yes. Passwords are never stored as plain text. They are salted and scrambled with PBKDF2 before they are saved."),
        Faq(10, "How do I delete my account?",
            "Go to Profile > Settings and tap Delete my account. Your orders and points are removed and cannot be recovered.")
    )

    val orders = listOf(
        SeedOrder(1001, 1, listOf(2 to 1, 6 to 2), "In 30 minutes", "Card Payment", OrderStatus.DELIVERED, "04 Oct 2026, 13:05"),
        SeedOrder(1002, 1, listOf(1 to 1, 10 to 1), "As soon as possible", "Cash on Collection", OrderStatus.DELIVERED, "01 Oct 2026, 18:20"),
        SeedOrder(1003, 2, listOf(24 to 1), "In 1 hour", "Card Payment", OrderStatus.DELIVERED, "06 Oct 2026, 17:45"),
        SeedOrder(1004, 3, listOf(4 to 2, 10 to 2, 7 to 2), "As soon as possible", "Cash on Collection", OrderStatus.DELIVERED, "06 Oct 2026, 12:30"),
        SeedOrder(1005, 4, listOf(15 to 1, 22 to 1), "In 30 minutes", "Card Payment", OrderStatus.DELIVERED, "07 Oct 2026, 19:10"),
        SeedOrder(1006, 5, listOf(11 to 1, 21 to 1, 8 to 2), "In 15 minutes", "Other Payment Options", OrderStatus.DELIVERED, "08 Oct 2026, 18:00"),
        SeedOrder(1007, 6, listOf(3 to 1, 10 to 1), "As soon as possible", "Cash on Collection", OrderStatus.DELIVERED, "08 Oct 2026, 13:40"),
        SeedOrder(1008, 7, listOf(25 to 2), "In 30 minutes", "Card Payment", OrderStatus.DELIVERED, "09 Oct 2026, 12:15"),
        SeedOrder(1009, 8, listOf(16 to 1, 6 to 1), "In 15 minutes", "Card Payment", OrderStatus.READY, "12 Oct 2026, 18:50"),
        SeedOrder(1010, 9, listOf(12 to 1, 5 to 1), "As soon as possible", "Cash on Collection", OrderStatus.PREPARING, "12 Oct 2026, 19:05"),
        SeedOrder(1011, 10, listOf(13 to 1, 10 to 1, 20 to 1), "In 1 hour", "Card Payment", OrderStatus.RECEIVED, "12 Oct 2026, 19:20"),
        SeedOrder(1012, 1, listOf(14 to 1, 9 to 1, 6 to 1), "In 30 minutes", "Card Payment", OrderStatus.PREPARING, "14 Oct 2026, 18:35")
    )

    val reviews = listOf(
        SeedReview(1, 2, "Malva Pudding", 5, "Best malva in town.", "Thabo N.", "05 Oct 2026"),
        SeedReview(2, 3, "Chips", 4, "Crispy, could use a bit more salt.", "Lerato K.", "03 Oct 2026"),
        SeedReview(3, 4, "Quarter Chicken", 5, "Juicy and full of flavour!", "Sipho M.", "02 Oct 2026"),
        SeedReview(4, 5, "Rump 300g", 5, "Perfectly cooked, tender and juicy.", "Naledi D.", "08 Oct 2026"),
        SeedReview(5, 6, "6 Wings", 4, "Lekker peri-peri heat, nice and crispy.", "Kagiso M.", "08 Oct 2026"),
        SeedReview(6, 7, "Cheese Burger", 4, "Big patty and fresh toppings.", "Zanele M.", "09 Oct 2026"),
        SeedReview(7, 8, "Family Combo", 5, "Fed the whole family, great value.", "Palesa S.", "10 Oct 2026"),
        SeedReview(8, 9, "Margherita Pizza", 3, "Tasty, but a little small for the price.", "Bongani Z.", "10 Oct 2026"),
        SeedReview(9, 10, "Chicken Wrap", 4, "Fresh and filling lunch option.", "Refilwe M.", "11 Oct 2026"),
        SeedReview(10, 1, "Overall experience", 5, "Quick service and the app made ordering easy.", "Demo Customer", "11 Oct 2026"),
        SeedReview(11, 2, "Pap & Gravy", 5, "Tastes just like home.", "Thabo N.", "12 Oct 2026"),
        SeedReview(12, 3, "Overall experience", 4, "My order was ready on time.", "Lerato K.", "12 Oct 2026")
    )

    val redemptions = listOf(
        SeedRedemption(1, 2, 1, "MON-4821", "03 Oct 2026"),
        SeedRedemption(2, 2, 2, "MON-1937", "05 Oct 2026"),
        SeedRedemption(3, 4, 1, "MON-7745", "04 Oct 2026"),
        SeedRedemption(4, 4, 4, "MON-2209", "06 Oct 2026"),
        SeedRedemption(5, 6, 1, "MON-5583", "07 Oct 2026"),
        SeedRedemption(6, 8, 3, "MON-3318", "08 Oct 2026"),
        SeedRedemption(7, 8, 5, "MON-9042", "09 Oct 2026"),
        SeedRedemption(8, 10, 1, "MON-6671", "09 Oct 2026"),
        SeedRedemption(9, 1, 4, "MON-1156", "10 Oct 2026"),
        SeedRedemption(10, 3, 1, "MON-8830", "11 Oct 2026")
    )

    val supportMessages = listOf(
        SeedSupportMessage(1, 2, "How long does a family combo usually take to prepare?", "04 Oct 2026"),
        SeedSupportMessage(2, 3, "Can I add a drink to my order after placing it?", "05 Oct 2026"),
        SeedSupportMessage(3, 4, "My loyalty points do not match my last order.", "06 Oct 2026"),
        SeedSupportMessage(4, 5, "Do you have a vegetarian option on the menu?", "07 Oct 2026"),
        SeedSupportMessage(5, 6, "Please can you confirm your opening hours on Sunday?", "08 Oct 2026"),
        SeedSupportMessage(6, 7, "I would like to change my collection time to later.", "08 Oct 2026"),
        SeedSupportMessage(7, 8, "Great food! Will you add more dessert options?", "09 Oct 2026"),
        SeedSupportMessage(8, 9, "I could not redeem my reward code at the till.", "10 Oct 2026"),
        SeedSupportMessage(9, 10, "Is it possible to pay with card when I collect?", "11 Oct 2026"),
        SeedSupportMessage(10, 1, "How do I update the name on my account?", "12 Oct 2026")
    )

    /** The menu in the form the app screens use (category shown by name). */
    fun foodItems(): List<FoodItem> = menuItems.map { item ->
        FoodItem(
            id = item.id,
            name = item.name,
            category = categories.first { it.id == item.categoryId }.name,
            description = item.description,
            price = item.price,
            emoji = item.emoji
        )
    }

    /** Order total = sum of the lines + the delivery fee, the same rule the cart uses. */
    fun orderTotal(order: SeedOrder): Double =
        order.lines.sumOf { (itemId, qty) -> menuItems.first { it.id == itemId }.price * qty } + DELIVERY_FEE
}
