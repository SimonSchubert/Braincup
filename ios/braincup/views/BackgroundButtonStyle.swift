//
//  BackgroundButtonStyle.swift
//  braincup
//
//  Created by Simon Schubert on 06.10.19.
//  Copyright © 2019 Simon Schubert. All rights reserved.
//

import Foundation
import SwiftUI

struct BackgroundButtonStyle: ButtonStyle {
    func makeBody(configuration: Self.Configuration) -> some View {
        configuration.label
            .padding(12)
            .foregroundColor(.white)
            .background(configuration.isPressed ? Color.red : Color(hex: 0xFFED7354))
            .cornerRadius(4)
    }
}

struct GreenButtonStyle: ButtonStyle {
    func makeBody(configuration: Self.Configuration) -> some View {
        configuration.label
            .padding(12)
            .foregroundColor(.white)
            .background(configuration.isPressed ? Color.green : Color(hex: "#5c8e58"))
            .cornerRadius(4)
    }
}

struct NumpadButtonStyle: ButtonStyle {
    func makeBody(configuration: Self.Configuration) -> some View {
        configuration.label
            .padding(16)
            .foregroundColor(.white)
            .background(configuration.isPressed ? Color.orange : Color(hex: 0xFFED7354))
            .cornerRadius(4)
            .frame(width: 50, height: 50)
    }
}
