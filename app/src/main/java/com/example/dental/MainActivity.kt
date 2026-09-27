package com.example.dental

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dental.ui.theme.DentalTheme
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.items //important for the list
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.dental.BookingRequest
import com.example.dental.CurvedBottomBarShape
import androidx.lifecycle.viewmodel.compose.viewModel



class MainActivity : ComponentActivity() {

override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DentalTheme {
                // Main App State
                var isLoggedIn by remember { mutableStateOf(false) }

                if (isLoggedIn) {
                    //BurgerMenuScreen()
                    BurgerMenuScreen(onLogout = { isLoggedIn = false })
                } else {
                    LoginRegisterScreen(
                        onLoginSuccess = { isLoggedIn = true }
                    )
                }
            }
        }
    }
}

// BURGER MENU FULLSCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BurgerMenuScreen(onLogout: () -> Unit = {}) {
    var isMenuOpen by remember { mutableStateOf(false) }
    var currentScreen by remember { mutableStateOf(AppScreen.DASHBOARD) }
    //FOR SCROLL HIDE/SHOW NAVIGATION
    var bottomBarHeightPx by remember { mutableFloatStateOf(0f) }
    var bottomBarOffsetPx by remember { mutableFloatStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                bottomBarOffsetPx = (bottomBarOffsetPx + delta).coerceIn(-bottomBarHeightPx, 0f)
                return Offset.Zero
            }
        }
    }

    //SHARED FUNCTION — Updates screen for both 3 lines Menu & Bottom Bar navigation
    val onNavigate: (AppScreen) -> Unit = { destination ->
        currentScreen = destination
        isMenuOpen = false
    }

    // 2. Use Box to layer the full-screen menu over the Scaffold dashboard
    Box(modifier = Modifier.fillMaxSize()) {


        // --- LAYER 2 BOTTOM NAVIGATION BUTTONS ---
        Scaffold(
            modifier = Modifier.nestedScroll(nestedScrollConnection),
            bottomBar = {
                AppBottomNavigation(
                    currentScreen = currentScreen,
                    onNavigate = onNavigate,
                    modifier = Modifier // --- NEW: hook this bar to the offset ---
                        .onSizeChanged { bottomBarHeightPx = it.height.toFloat() }
                        .offset { IntOffset(x = 0, y = -bottomBarOffsetPx.roundToInt()) }
                )
            },

            // --- LAYER 1 MAIN DASHBOARD ---
            //TopAppBar
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = when(currentScreen) {
                                AppScreen.DASHBOARD -> "Dental Dashboard"
                                AppScreen.APPOINTMENTS -> "Appointments"
                                AppScreen.PATIENTS -> "Patient Records"
                                AppScreen.SETTINGS -> "Settings"
                            },

                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { isMenuOpen = true }) {
                            CustomCircularBurgerIcon()
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFE0F2FE)
                    )
                )
            }
        ) { innerPadding ->
            //SWITCH CONTENT HERE based on selected menu item
            Box(modifier = Modifier.padding(innerPadding)) {
                when (currentScreen) {
                    AppScreen.DASHBOARD -> DashboardScreen()
                    AppScreen.APPOINTMENTS -> AppointmentsScreen()
                    AppScreen.PATIENTS -> PatientRecordsScreen()
                    AppScreen.SETTINGS -> SettingsScreen()
                }
            }
        }

        // LAYER 2 Animated Overlay
        AnimatedVisibility(
            visible = isMenuOpen,
            enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
        ) {
            FullscreenMenuOverlay(
                onClose = { isMenuOpen = false },
                onNavigate = { destination ->
                    currentScreen = destination // Change the active screen
                    isMenuOpen = false         // Close the menu overlay
                },
                onLogout = {
                    isMenuOpen = false
                    onLogout()
                }
            )
        }
    }
}

//NAV LINKS
@Composable
fun DashboardScreen() {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFE0F2FE)),
        contentAlignment = Alignment.Center
    ) {
        Text("Welcome to Dashboard!")
    }
}

@Composable
fun AppointmentsScreen(viewModel: BookingViewModel = viewModel()) {
    //Automatically fetch data from Render when this screen opens
    LaunchedEffect(Unit) {
        viewModel.fetchAppointments()
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFE0F2FE)) // Your Light Blue
    ) {
        // 3. Check if we have data
        if (viewModel.appointments.isEmpty()) {
            Text("No appointments yet...", modifier = Modifier.align(Alignment.Center))
        } else {
            // 4. Show the scrollable list of real web bookings!
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(viewModel.appointments) { booking ->
                    // A nice White Card for each booking
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = booking.fullname, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(text = booking.service, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text(text = "📅 ${booking.date}", fontSize = 14.sp)
                                Text(text = "⏰ ${booking.time}", fontSize = 14.sp, color = Color(0xFF0284C7))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PatientRecordsScreen() {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFE0F2FE)),
        contentAlignment = Alignment.Center
    ) {
        Text("Patient Records Screen")
    }
}

@Composable
fun SettingsScreen() {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFE0F2FE)),
        contentAlignment = Alignment.Center
    ) {
        Text("Settings Screen")
    }
}

// ———————————————————————————————————————————————————————————————————————
// LOGIN / REGISTER SCREEN (Shown First)
// ———————————————————————————————————————————————————————————————————————
@Composable
fun LoginRegisterScreen(onLoginSuccess: () -> Unit) {
    // Hardcoded accounts list (Pre-populated with 1 default user)
    //  Put this ABOVE "class MainActivity"
    data class User(val email: String, val pass: String)

//Inside LoginRegisterScreen, use it like this:
    val userList = remember {
        mutableStateListOf(
            User(email = "1", pass = "1")
        )
    }


    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0F2FE))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .padding(top = 23.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)


            ) {
                Text(
                    text = "Welcome Back",
                    fontSize = 24.sp,
                    color = Color(0xFF0284C7)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (email.isNotEmpty() && password.isNotEmpty()) {
                            onLoginSuccess() // Triggers 'isLoggedIn = true' in MainActivity
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                ) {
                    Text("Login", fontSize = 16.sp)
                }
            }
        }
    }
}


//BOTTOM FUNCTION NAVIGATION WITH CURVED DESIGN CONNECTED TO THE SHOW/HIDE NAVIGATION
@Composable
fun AppBottomNavigation(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    onCenterButtonClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(90.dp)
            .background(Color(0xFFE0F2FE)),
        contentAlignment = Alignment.BottomCenter
    ) {
        //CURVED NAVIGATIONCENTER STYLE
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .clip(CurvedBottomBarShape()),
            color = Color.White,
            tonalElevation = 8.dp,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Item 1 Dashboard
                CustomNavItem(
                    icon = Icons.Default.Home,
                    label = "Dashboard",
                    isSelected = currentScreen == AppScreen.DASHBOARD,
                    onClick = { onNavigate(AppScreen.DASHBOARD) }
                )

                // Left Item 2 Appointments
                CustomNavItem(
                    icon = Icons.Default.DateRange,
                    label = "Appointments",
                    isSelected = currentScreen == AppScreen.APPOINTMENTS,
                    onClick = { onNavigate(AppScreen.APPOINTMENTS) }
                )

                // Spacer for the Center Floating Circle
                Spacer(modifier = Modifier.width(56.dp))

                // Right Item 1 Patients
                CustomNavItem(
                    icon = Icons.Default.Person,
                    label = "Patients",
                    isSelected = currentScreen == AppScreen.PATIENTS,
                    onClick = { onNavigate(AppScreen.PATIENTS) }
                )

                // Right Item 2 Settings
                CustomNavItem(
                    icon = Icons.Default.Settings,
                    label = "Settings",
                    isSelected = currentScreen == AppScreen.SETTINGS,
                    onClick = { onNavigate(AppScreen.SETTINGS) }
                )
            }
        }


            // --- LAYER 2 Floating Center Circle Button ---
            FloatingActionButton(
                onClick = onCenterButtonClick,
                shape = CircleShape,
                containerColor = Color(0xFF0284C7),
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 4.dp)
                    .size(54.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Quick Add Appointment"
                )
            }
        }
    }


// Helper Composable for Individual Navigation Items Designs and adjustments
@Composable
private fun RowScope.CustomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val tint = if (isSelected) Color(0xFF0284C7) else Color.Gray

    //IconButton(
        //onClick = onClick,
        //modifier = Modifier.size(50.dp)
    //) {
    Column(
        modifier = Modifier
            .weight(1f) //weight works because of RowScope
            .clickable { onClick() } // whole area clickable
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = tint
            )
        }
    }



// FULLSCREEN BRUGER MENU
@Composable
fun FullscreenMenuOverlay(
    onClose: () -> Unit,
    onNavigate: (AppScreen) -> Unit,
    onLogout: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0F172A)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = 8.dp),
            verticalArrangement = Arrangement.Top
        ) {


            // Header: Title & Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {


                Text(
                    text = "DENTAL PRO",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF38BDF8),
                    letterSpacing = 2.sp
                )



                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Menu",
                        tint = Color.White
                    )
                }
            }
            //SPACE FOR TOP
            Spacer(modifier = Modifier.height(50.dp))

            // Navigation Links
            //Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                //MenuNavItem(title = "Dashboard", subtitle = "Overview & Stats") { onClose() }
                //MenuNavItem(title = "Appointments", subtitle = "Manage schedule") { onClose() }
                //MenuNavItem(title = "Patient Records", subtitle = "History & details") { onClose() }
                //MenuNavItem(title = "Settings", subtitle = "App preferences") { onClose() }
            //}

            // Navigation Links
            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                MenuNavItem(title = "Dashboard", subtitle = "Overview & Stats") {
                    onNavigate(AppScreen.DASHBOARD)
                }
                MenuNavItem(title = "Appointments", subtitle = "Manage schedule") {
                    onNavigate(AppScreen.APPOINTMENTS)
                }
                MenuNavItem(title = "Patient Records", subtitle = "History & details") {
                    onNavigate(AppScreen.PATIENTS)
                }
                MenuNavItem(title = "Settings", subtitle = "App preferences") {
                    onNavigate(AppScreen.SETTINGS)
                }
            }
            //SPACE LINE GAP
            Spacer(modifier = Modifier.weight(1f))

            // Bottom Action
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Red.copy(alpha = 0.15f))
                    .clickable { onLogout() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Log Out",
                    color = Color(0xFFEF4444),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MenuNavItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = subtitle,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

enum class AppScreen {
    DASHBOARD,
    APPOINTMENTS,
    PATIENTS,
    SETTINGS
}


@Composable
fun CustomCircularBurgerIcon() {
    // Background Circle
    Box(
        modifier = Modifier
            .size(42.dp) // Size of the circle
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.2f)), // Changed to Red
        contentAlignment = Alignment.Center
    ) {
        //2 Horizontal Lines
        Column(
            modifier = Modifier.width(20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Line 1 Top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Color.Black) // Changed to Red
            )
            // Line 2 Top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Color.Black))
            // Line 3Top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    //.fillMaxWidth(0.8f) // Shorter bottom line
                    .height(2.dp)
                    .background(Color.Black) // Changed to Red
            )
        }
    }
}






// FULLSCREEN BURGER MENU


// BURGER MENU SCREEN OLD
/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BurgerMenuScreen() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                //Drawer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(Color(0xFF0284C7)),
                    contentAlignment = Alignment.CenterStart
                ){
                    Text(
                        text = "Dental App Menu",
                        color = Color.White,
                        fontSize = 22.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
        HorizontalDivider()
        // Drawer Items
        NavigationDrawerItem(
            label = { Text("Settings") },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            selected = false,
            onClick = { scope.launch { drawerState.close() } }
        )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Dental Dashboard") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Open Burger Menu"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFE0F2FE)
                    )
                )
            }
        ) { innerPadding ->
            Dental(
                name = "Android",
                modifier = Modifier.padding(innerPadding) // Keeps content below the top app bar!
            )
        }
    }
}
// BURGER MENU SCREEN
*/




// Main editor for mobile app
@Composable
fun Dental(name: String, modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0F2FE))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = modifier
                .padding(top = 80.dp)
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Welcome to my Dental App!",
                fontSize = 20.sp,
                color = Color.Cyan
            )
        }
    }
}
// Main editor for mobile app

@Preview(showBackground = true)
@Composable
fun DentalPreview() {
    DentalTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Dental(
                name = "Android",
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}