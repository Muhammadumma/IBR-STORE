package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// --- Premium Fintech Minimal Color Palette ---

// Primary Accent (Refined Emerald Green - Apple / Stripe / Revolut grade)
val FintechEmerald = Color(0xFF059669) // Primary Action & Vital Positive Highlights
val FintechEmeraldDark = Color(0xFF047857)
val FintechEmeraldLight = Color(0xFFECFDF5)
val EmeraldAccent = FintechEmerald // Alias for compatibility
val EmeraldGlow = Color(0xFF10B981)

// Clean Light Canvas & Neutral Gray Layers
val PureWhite = Color(0xFFFFFFFF)
val NeutralBackground = Color(0xFFF5F5F7) // Very light neutral gray for depth without harsh borders
val NeutralSurface = Color(0xFFFFFFFF)
val NeutralSurfaceVariant = Color(0xFFF3F4F6) // Soft card/input background
val NeutralBorder = Color(0xFFE5E7EB) // Subtle light border
val NeutralBorderSubtle = Color(0x0F000000)

// Deep Charcoal Typography Hierarchy
val DeepCharcoal = Color(0xFF111827) // Primary Headings & Product Titles
val FintechCharcoal = DeepCharcoal
val TextSecondary = Color(0xFF4B5563) // Medium-weight subheadings
val TextMuted = Color(0xFF9CA3AF) // Subtle hints, secondary labels

// Refined Minimalist Dark Palette (Charcoal / Slate instead of noisy green)
val DarkCharcoalBg = Color(0xFF111418)
val DarkCharcoalSurface = Color(0xFF1B1F24)
val DarkCharcoalSurfaceVariant = Color(0xFF242A32)
val DarkCharcoalBorder = Color(0xFF2D333B)
val DarkTextPrimary = Color(0xFFF3F4F6)
val DarkTextSecondary = Color(0xFF9CA3AF)
val DarkTextMuted = Color(0xFF6B7280)

// Legacy Aliases for Compatibility
val BrandBluePrimary = Color(0xFF0F766E)
val BrandBlueHover = Color(0xFF115E59)
val BrandBlueLight = Color(0xFFF0FDFA)

val SurfaceLight = PureWhite
val BackgroundLight = NeutralBackground
val BorderLight = NeutralBorder
val TextMainLight = DeepCharcoal
val TextSecondaryLight = TextSecondary
val TextMutedLight = TextMuted

val DarkGreenBackground = DarkCharcoalBg
val DarkGreenSurface = DarkCharcoalSurface
val DarkGreenSurfaceCard = DarkCharcoalSurfaceVariant
val DarkGreenBorder = DarkCharcoalBorder

val TextMainDark = DarkTextPrimary
val TextSecondaryDark = DarkTextSecondary
val TextMutedDark = DarkTextMuted

// Functional Status Colors
val SuccessGreen = Color(0xFF059669)
val SuccessBgLight = Color(0xFFECFDF5)
val WarningAmber = Color(0xFFD97706)
val WarningBgLight = Color(0xFFFFFBEB)
val DangerRed = Color(0xFFDC2626)
val DangerBgLight = Color(0xFFFEF2F2)
val InfoBlue = Color(0xFF2563EB)
val InfoBgLight = Color(0xFFEFF6FF)
